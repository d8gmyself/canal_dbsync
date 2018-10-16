package com.d8gmyself.dbsync.etl.transform.transformer.impl;

import com.alibaba.fastjson.JSON;
import com.d8gmyself.dbsync.commons.model.ColumnPair;
import com.d8gmyself.dbsync.commons.model.DataMediaPair;
import com.d8gmyself.dbsync.commons.model.Pipeline;
import com.d8gmyself.dbsync.etl.commons.enums.EventType;
import com.d8gmyself.dbsync.etl.commons.model.EventColumn;
import com.d8gmyself.dbsync.etl.commons.model.EventData;
import com.d8gmyself.dbsync.etl.transform.exception.TransformException;
import com.d8gmyself.dbsync.etl.transform.transformer.Transformer;
import com.d8gmyself.dbsync.utils.ConfigHelper;
import com.d8gmyself.dbsync.utils.PipelineHelper;
import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import org.springframework.beans.BeanUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by ZhangDuo on 2016-3-23 12:13.
 * <p>
 * 默认数据转换器
 *
 * @author ZhangDuo
 */
class DefaultTransformer implements Transformer {

    /**
     * 渠道配置
     */
    protected final Pipeline pipeline;

    DefaultTransformer(Pipeline pipeline) {
        this.pipeline = pipeline;
    }


    @Override
    public List<EventData> transform(List<EventData> datas) {
        List<EventData> result = Lists.newArrayList();
        for (EventData data : datas) {
            //获取当前数据的映射配置
            List<DataMediaPair> dataMediaPairs = PipelineHelper.getDataMediaPairBySchemaAndTable(pipeline, data.getSchemaName(), data.getTableName());
            for (final DataMediaPair dataMediaPair : dataMediaPairs) {
                //当前Event对目标库是否有影响，DELETE或者目标库关心的字段有update时，add为true
                boolean add = false;
                EventData eventData = new EventData();
                BeanUtils.copyProperties(data, eventData);
                eventData.setSchemaName(dataMediaPair.getTargetSchema());
                eventData.setTableName(dataMediaPair.getTargetTableName());
                //字段映射
                if (!dataMediaPair.getColumnPairs().isEmpty()) {
                    //过滤不关心字段
                    List<EventColumn> eventColumns = data.getColumns().stream()
                            .filter(eventColumn -> ConfigHelper.getColumnPairBySrcName(dataMediaPair.getColumnPairs(), eventColumn.getName()) != null)
                            .collect(Collectors.toList());
                    //对字段做转换（字段名、默认值）
                    for (EventColumn eventColumn : eventColumns) {
                        ColumnPair columnPair = ConfigHelper.getColumnPairBySrcName(dataMediaPair.getColumnPairs(), eventColumn.getName());
                        if (columnPair != null) {
                            eventColumn.setName(columnPair.getTargetName());
                            if (eventColumn.isNull() && columnPair.getDefaultValue() != null) {
                                eventColumn.setNull(false);
                                eventColumn.setValue(columnPair.getDefaultValue());
                            }
                            add |= eventColumn.isUpdate();
                        }
                    }
                    eventData.setColumns(eventColumns);
                } else {
                    add = true;
                }
                if (data.getEventType().isDelete() || add) {
                    result.add(eventData);
                }
            }
        }
        datas.clear();
        return result;
    }

    /**
     * {@inheritDoc}
     *
     * @throws TransformException 由{@link #innerMerge(EventData, EventData)}抛出
     */
    @Override
    public List<EventData> merge(List<EventData> datas) {
        Map<String, EventData> map = new HashMap<>();
        for (final EventData eventData : datas) {
            EventData lastEvent = map.get(eventData.getKey());
            if (lastEvent == null) {
                map.put(eventData.getKey(), eventData);
            } else {
                innerMerge(lastEvent, eventData);
            }
        }
        datas.clear();
        List<EventData> result = Lists.newArrayList(map.values());
        result.sort((o1, o2) -> o1.getIndex() - o2.getIndex() > 0 ? 1 : o1.getIndex() - o2.getIndex() == 0 ? 0 : -1);
        return result;
    }

    /**
     * event合并
     * <p/>
     * <pre>
     * 合并规则：<br />
     * insert + insert : error
     * insert + update : insert
     * insert + delete : delete
     * update + insert : error
     * update + update : update
     * update + delete : delete
     * delete + insert : update
     * delete + update : error
     * delete + delete : delete
     * </pre>
     *
     * @param lastEvent 上一事件
     * @param currEvent 当前事件
     * @throws TransformException 当上述合并结果为error时，抛出该异常
     */
    private void innerMerge(EventData lastEvent, EventData currEvent) {
        List<EventColumn> lastEventColumns = lastEvent.getColumns();
        boolean throwException = false;
        //EVENT合并
        if (lastEvent.getEventType().isInsert() || lastEvent.getEventType().isUpdate()) {
            if (currEvent.getEventType().isInsert()) {
                throwException = true;
            } else if (currEvent.getEventType().isUpdate()) {
                lastEvent.setColumns(currEvent.getColumns());
                lastEvent.setExecuteTime(currEvent.getExecuteTime());
                lastEvent.setIndex(currEvent.getIndex());
            } else if (currEvent.getEventType().isDelete()) {
                lastEvent.setEventType(EventType.DELETE);
                lastEvent.setColumns(currEvent.getColumns());
                lastEvent.setExecuteTime(currEvent.getExecuteTime());
                lastEvent.setIndex(currEvent.getIndex());
            }
        } else if (lastEvent.getEventType().isDelete()) {
            if (currEvent.getEventType().isInsert()) {
                lastEvent.setEventType(EventType.UPDATE);
                lastEvent.setColumns(currEvent.getColumns());
                lastEvent.setExecuteTime(currEvent.getExecuteTime());
                lastEvent.setIndex(currEvent.getIndex());
            } else if (currEvent.getEventType().isUpdate()) {
                throwException = true;
            } else if (currEvent.getEventType().isDelete()) {
                lastEvent.setColumns(currEvent.getColumns());
                lastEvent.setExecuteTime(currEvent.getExecuteTime());
                lastEvent.setIndex(currEvent.getIndex());
            }
        }
        if (throwException) {
            throw new TransformException("merge event error, last:" + JSON.toJSONString(lastEvent) + ",current:" + JSON.toJSONString(currEvent));
        }
        //判断字段是否更新以及oldValue
        int index = 0;
        for (EventColumn column : lastEvent.getColumns()) {
            column.setOldValue(lastEventColumns.get(index).getOldValue());
            column.setUpdate((lastEventColumns.get(index).isUpdate() || column.isUpdate()) && !Objects.equal(column.getValue(), column.getOldValue()));
            index++;
        }
    }

}

package com.d8gmyself.dbsync.etl.select.selector;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.d8gmyself.dbsync.etl.commons.enums.EventType;
import com.d8gmyself.dbsync.etl.commons.model.EventColumn;
import com.d8gmyself.dbsync.etl.commons.model.EventData;
import com.d8gmyself.dbsync.etl.select.exception.SelectException;
import com.google.common.collect.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * Created by ZhangDuo on 2016-3-10 11:04.
 * <p>
 * 解析Canal的Message
 *
 * @author ZhangDuo
 */
public class MessageParser {

    /**
     * 将从canal获取的{@link CanalEntry.Entry}解析为{@link EventData}
     *
     * @param pipelineId 配置id
     * @param datas      {@link CanalEntry.Entry}数据
     * @return 解析后的数据
     */
    public static List<EventData> parse(@SuppressWarnings("unused") Long pipelineId, List<CanalEntry.Entry> datas) {
        List<EventData> result = Lists.newArrayList();
        long n = 0;
        try {
            for (CanalEntry.Entry entry : datas) {
                if (entry.getEntryType() == CanalEntry.EntryType.ROWDATA && entry.getHeader().getEventType() != CanalEntry.EventType.DELETE) {
                    final CanalEntry.RowChange rowChange = CanalEntry.RowChange.parseFrom(entry.getStoreValue());
                    EventData eventData = new EventData();
                    eventData.setTableName(entry.getHeader().getTableName());
                    eventData.setSchemaName(entry.getHeader().getSchemaName());
                    eventData.setExecuteTime(entry.getHeader().getExecuteTime());
                    eventData.setEventType(convertEventType(entry.getHeader().getEventType()));
                    for (CanalEntry.RowData rowData : rowChange.getRowDatasList()) {
                        result.add(innerParse(rowData, eventData, n++));
                    }
                }
            }
        } catch (Exception e) {
            throw new SelectException(e);
        }
        return result;
    }

    /**
     * 解析canal中的RowData为setl中的数据模型，主要解析字段信息
     *
     * @param rowData   rowData
     * @param eventData eventData
     * @return 解析后的数据
     */
    private static EventData innerParse(final CanalEntry.RowData rowData, final EventData eventData, long n) {
        List<EventColumn> columns = Lists.newArrayList();
        EventData _eventData = new EventData();
        BeanUtils.copyProperties(eventData, _eventData);
        StringBuilder key = new StringBuilder(eventData.getSchemaName()).append(".").append(eventData.getTableName());
        if (eventData.getEventType().isInsert() || eventData.getEventType().isUpdate()) {
            for (CanalEntry.Column column : rowData.getAfterColumnsList()) {
                EventColumn eventColumn = new EventColumn();
                eventColumn.setKey(column.getIsKey());
                eventColumn.setName(column.getName());
                eventColumn.setNull(column.getIsNull());
                eventColumn.setSqlType(column.getSqlType());
                eventColumn.setOldValue(CollectionUtils.isEmpty(rowData.getBeforeColumnsList()) ? null : rowData.getBeforeColumns(column.getIndex()).getValue());
                eventColumn.setValue(column.getValue());
                eventColumn.setUpdate(column.getUpdated());
                columns.add(eventColumn);
                if (eventColumn.isKey()) {
                    key.append(".").append(eventColumn.getValue());
                }
            }
        } else if (eventData.getEventType().isDelete()) {
            for (CanalEntry.Column column : rowData.getBeforeColumnsList()) {
                EventColumn eventColumn = new EventColumn();
                eventColumn.setKey(column.getIsKey());
                eventColumn.setName(column.getName());
                eventColumn.setNull(column.getIsNull());
                eventColumn.setSqlType(column.getSqlType());
                eventColumn.setValue(column.getValue());
                columns.add(eventColumn);
                if (column.getIsKey()) {
                    key.append(".").append(eventColumn.getValue());
                }
            }
        }
        _eventData.setColumns(columns);
        _eventData.setKey(key.toString());
        _eventData.setIndex(n);
        return _eventData;
    }

    private static EventType convertEventType(CanalEntry.EventType type) {
        switch (type) {
            case INSERT:
                return EventType.INSERT;
            case UPDATE:
                return EventType.UPDATE;
            case DELETE:
                return EventType.DELETE;
            default:
                return EventType.UNKNOWN;
        }
    }

}

package com.d8gmyself.dbsync.etl.transform.transformer;

import com.d8gmyself.dbsync.etl.commons.model.EventData;

import java.util.List;

/**
 * Created by ZhangDuo on 2016-3-23 12:09.
 * <p>
 * 数据转换器
 *
 * @author ZhangDuo
 */
public interface Transformer {

    /**
     * 转换原数据为目标数据
     *
     * @param datas 原数据
     * @return 目标数据
     */
    List<EventData> transform(List<EventData> datas);

    /**
     * 合并同一条记录的所有操作为一个最终操作
     *
     * @param datas 要合并的所有数据
     * @return 合并结果
     */
    List<EventData> merge(List<EventData> datas);

}

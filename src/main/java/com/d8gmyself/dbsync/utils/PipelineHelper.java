package com.d8gmyself.dbsync.utils;

import com.d8gmyself.dbsync.commons.model.DataMediaPair;
import com.d8gmyself.dbsync.commons.model.Pipeline;

import java.util.Collections;
import java.util.List;

/**
 * Created by ZhangDuo on 2016-3-10 14:23.
 * <p>
 * 渠道辅助类
 *
 * @author ZhangDuo
 */
public class PipelineHelper {

    private PipelineHelper() {

    }

    /**
     * 获取指定渠道、库名和表名的映射配置
     *
     * @param pipeline 渠道
     * @param schema   库名
     * @param table    表名
     * @return 映射信息
     */
    public static List<DataMediaPair> getDataMediaPairBySchemaAndTable(Pipeline pipeline, String schema, String table) {
        return pipeline.getDataMediaPairs().getOrDefault(schema + "." + table, Collections.emptyList());
    }

}

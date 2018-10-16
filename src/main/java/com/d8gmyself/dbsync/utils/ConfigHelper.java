package com.d8gmyself.dbsync.utils;

import com.d8gmyself.dbsync.commons.model.ColumnPair;

import java.util.Map;

/**
 * Created by ZhangDuo on 2016-3-10 16:04.
 * <p>
 * config辅助类
 *
 * @author ZhangDuo
 */
public class ConfigHelper {

    private ConfigHelper() {}

    /**
     * 从给定的字段映射列表里面获取给定字段名的映射信息
     *
     * @param columnPairs   字段映射列表
     * @param srcColumnName 原字段名
     * @return 字段映射信息
     */
    public static ColumnPair getColumnPairBySrcName(Map<String, ColumnPair> columnPairs, String srcColumnName) {
        return columnPairs.get(srcColumnName);
    }

}

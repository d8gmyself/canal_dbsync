package com.d8gmyself.dbsync.commons.model;

import java.util.Map;

/**
 * Created by ZhangDuo on 2016-3-10 13:34.
 * <p>
 * 库表映射关系
 *
 * @author ZhangDuo
 */
public class DataMediaPair {

    /**
     * 渠道id
     */
    private Long pipelineId;
    /**
     * 原库名
     */
    private String srcSchema;
    /**
     * 目标库名
     */
    private String targetSchema;
    /**
     * 原表名
     */
    private String srcTableName;
    /**
     * 目标表名
     */
    private String targetTableName;
    /**
     * 字段映射，若为empty，则照搬原表字段，key为ColumnPair的srcName
     */
    private Map<String, ColumnPair> columnPairs;
    /**
     * 序号
     */
    private Integer id;

    public Long getPipelineId() {
        return pipelineId;
    }

    public void setPipelineId(Long pipelineId) {
        this.pipelineId = pipelineId;
    }

    public String getSrcSchema() {
        return srcSchema;
    }

    public void setSrcSchema(String srcSchema) {
        this.srcSchema = srcSchema;
    }

    public String getTargetSchema() {
        return targetSchema;
    }

    public void setTargetSchema(String targetSchema) {
        this.targetSchema = targetSchema;
    }

    public String getSrcTableName() {
        return srcTableName;
    }

    public void setSrcTableName(String srcTableName) {
        this.srcTableName = srcTableName;
    }

    public String getTargetTableName() {
        return targetTableName;
    }

    public void setTargetTableName(String targetTableName) {
        this.targetTableName = targetTableName;
    }

    public Map<String, ColumnPair> getColumnPairs() {
        return columnPairs;
    }

    public void setColumnPairs(Map<String, ColumnPair> columnPairs) {
        this.columnPairs = columnPairs;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}

package com.d8gmyself.dbsync.etl.commons.model;

import com.d8gmyself.dbsync.etl.commons.enums.EventType;

import java.util.Collections;
import java.util.List;

/**
 * Created by ZhangDuo on 2016-3-10 10:20.
 * <p>
 * 在etl中流转的行数据模型
 *
 * @author ZhangDuo
 */
public class EventData {
    /**
     * schemaName
     */
    private String schemaName;
    /**
     * 表名
     */
    private String tableName;
    /**
     * 操作类型
     */
    private EventType eventType;
    /**
     * 执行时间
     */
    private long executeTime;
    /**
     * 字段
     */
    private List<EventColumn> columns = Collections.emptyList();
    /**
     * sql语句
     */
    private String sql;
    /**
     * 主键值，若为联合主键，格式为schemaName.tableName.key1.key2.key3......
     */
    private String key;
    /**
     * 序号，保证binlog执行顺序
     */
    private long index;

    public String getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public long getExecuteTime() {
        return executeTime;
    }

    public void setExecuteTime(long executeTime) {
        this.executeTime = executeTime;
    }

    public List<EventColumn> getColumns() {
        return columns;
    }

    public void setColumns(List<EventColumn> columns) {
        this.columns = columns;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public long getIndex() {
        return index;
    }

    public void setIndex(long index) {
        this.index = index;
    }
}

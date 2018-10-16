package com.d8gmyself.dbsync.etl.commons.model;

/**
 * Created by ZhangDuo on 2016-3-8 13:49.
 * <p>
 * 在etl中流转的列数据模型
 *
 * @author ZhangDuo
 */
public class EventColumn {

    /**
     * 列类型
     */
    private int sqlType;
    /**
     * 列名
     */
    private String name;
    /**
     * 老值，若为insert，oldValue为null
     */
    private String oldValue;
    /**
     * 新值
     */
    private String value;
    /**
     * 是否NULL值
     */
    private boolean isNull;
    /**
     * 是否主键
     */
    private boolean isKey;
    /**
     * 是否更新
     */
    private boolean isUpdate;

    public int getSqlType() {
        return sqlType;
    }

    public void setSqlType(int sqlType) {
        this.sqlType = sqlType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOldValue() {
        return oldValue;
    }

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isNull() {
        return isNull;
    }

    public void setNull(boolean aNull) {
        isNull = aNull;
    }

    public boolean isKey() {
        return isKey;
    }

    public void setKey(boolean key) {
        isKey = key;
    }

    public boolean isUpdate() {
        return isUpdate;
    }

    public void setUpdate(boolean update) {
        isUpdate = update;
    }
}

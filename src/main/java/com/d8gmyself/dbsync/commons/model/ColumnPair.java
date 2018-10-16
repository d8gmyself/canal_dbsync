package com.d8gmyself.dbsync.commons.model;

/**
 * Created by ZhangDuo on 2016-3-10 13:36.
 * <p>
 * 字段映射关系
 *
 * @author ZhangDuo
 */
public class ColumnPair {

    /**
     * 原字段名
     */
    private String srcName;
    /**
     * 目标字段名
     */
    private String targetName;
    /**
     * 原字段为NULL时目标字段的默认值(其实可以由数据库默认值来完成)
     */
    private String defaultValue;

    public String getSrcName() {
        return srcName;
    }

    public void setSrcName(String srcName) {
        this.srcName = srcName;
    }

    public String getTargetName() {
        return targetName;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }
}

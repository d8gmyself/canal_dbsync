package com.d8gmyself.dbsync.etl.commons.enums;

/**
 * Created by ZhangDuo on 2016-3-10 10:21.
 * <p>
 * BinLong Event枚举，只列举业务关心的INSERT/UPDATE/DELETE
 *
 * @author ZhangDuo
 */
public enum EventType {

    /**
     * 插入行
     */
    INSERT("I"),

    /**
     * 更新行
     */
    UPDATE("U"),

    /**
     * 删除行
     */
    DELETE("D"),

    /**
     * 未知
     */
    UNKNOWN("N");

    private String value;

    EventType(String value) {
        this.value = value;
    }

    public boolean isInsert() {
        return this.equals(EventType.INSERT);
    }

    public boolean isUpdate() {
        return this.equals(EventType.UPDATE);
    }

    public boolean isDelete() {
        return this.equals(EventType.DELETE);
    }

    public static EventType valuesOf(String value) {
        EventType[] eventTypes = values();
        for (EventType eventType : eventTypes) {
            if (eventType.value.equalsIgnoreCase(value)) {
                return eventType;
            }
        }
        return null;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}

package com.d8gmyself.dbsync.arbitrate.event;


/**
 * Created by ZhangDuo on 2016-3-10 12:41.
 * <p>
 * 调度事件
 *
 * @author ZhangDuo
 */
public abstract class ArbitrateEvent<T> {

    private final T data;

    public ArbitrateEvent(T data) {
        this.data = data;
    }

    public T getData() {
        return data;
    }
}

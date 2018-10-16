package com.d8gmyself.dbsync.arbitrate.event;

/**
 * Created by ZhangDuo on 2016-3-10 12:44.
 * <p>
 * LOAD事件
 *
 * @author ZhangDuo
 */
public class LoadArbitrateEvent extends ArbitrateEvent<ETLEventData> {

    public LoadArbitrateEvent(ETLEventData data) {
        super(data);
    }

}

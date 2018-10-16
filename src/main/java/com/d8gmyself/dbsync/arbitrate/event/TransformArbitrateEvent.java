package com.d8gmyself.dbsync.arbitrate.event;

/**
 * Created by ZhangDuo on 2016-3-10 12:44.
 * <p>
 * TRANSFORM事件
 *
 * @author ZhangDuo
 */
public class TransformArbitrateEvent extends ArbitrateEvent<ETLEventData> {

    public TransformArbitrateEvent(ETLEventData data) {
        super(data);
    }

}

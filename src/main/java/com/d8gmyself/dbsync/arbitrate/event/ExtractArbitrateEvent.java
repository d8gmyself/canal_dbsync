package com.d8gmyself.dbsync.arbitrate.event;

/**
 * Created by ZhangDuo on 2016-3-10 12:44.
 * <p>
 * EXTRACT事件
 *
 * @author ZhangDuo
 */
public class ExtractArbitrateEvent extends ArbitrateEvent<ETLEventData> {

    public ExtractArbitrateEvent(ETLEventData data) {
        super(data);
    }
}

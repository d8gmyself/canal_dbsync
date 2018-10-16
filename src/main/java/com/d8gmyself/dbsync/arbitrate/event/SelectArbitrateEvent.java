package com.d8gmyself.dbsync.arbitrate.event;

/**
 * Created by ZhangDuo on 2016-3-10 12:44.
 * <p>
 * SELECT事件
 *
 * @author ZhangDuo
 */
public class SelectArbitrateEvent extends ArbitrateEvent<ETLEventData> {

    public SelectArbitrateEvent(ETLEventData data) {
        super(data);
    }

}

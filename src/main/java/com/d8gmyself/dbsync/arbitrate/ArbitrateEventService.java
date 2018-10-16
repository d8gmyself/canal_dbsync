package com.d8gmyself.dbsync.arbitrate;

import com.d8gmyself.dbsync.arbitrate.event.ETLEventData;
import com.d8gmyself.dbsync.arbitrate.event.TerminEventData;

/**
 * Created by ZhangDuo on 2016-3-10 12:39.
 * <p>
 * 调度服务
 *
 * @author ZhangDuo
 */
public interface ArbitrateEventService {

    /**
     * 发送select事件
     */
    void selected(ETLEventData data);

    /**
     * 发送extract事件
     */
    void extracted(ETLEventData data);

    /**
     * 发送transform事件
     */
    void transformed(ETLEventData data);

    /**
     * 发送load事件
     */
    void loaded(ETLEventData data);

    /**
     * 发送termin事件
     */
    void terminated(TerminEventData data);

    /**
     * 注册事件处理器
     */
    void register(Object listener);
}

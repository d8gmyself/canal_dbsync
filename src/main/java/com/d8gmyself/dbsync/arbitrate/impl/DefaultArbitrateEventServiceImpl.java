package com.d8gmyself.dbsync.arbitrate.impl;

import com.d8gmyself.core.eventbus.EventBus;
import com.d8gmyself.dbsync.arbitrate.ArbitrateEventService;
import com.d8gmyself.dbsync.arbitrate.event.*;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by ZhangDuo on 2016-3-10 12:49.
 * <p>
 * 默认的setl流程调度服务
 *
 * @author ZhangDuo
 */
public class DefaultArbitrateEventServiceImpl implements ArbitrateEventService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultArbitrateEventServiceImpl.class);

    private final EventBus eventBus;
    public DefaultArbitrateEventServiceImpl() {
        int threadNums = Runtime.getRuntime().availableProcessors() * 2;
        ExecutorService executor = new ThreadPoolExecutor(threadNums, threadNums, 5, TimeUnit.MINUTES, new LinkedBlockingQueue<>(1024),
                new ThreadFactoryBuilder().setNameFormat("arbitrate-thread-%d").build());
        eventBus = EventBus.builder().setIdentifier("arbitrate-event-bus")
                .setExecutor(executor)
                .setSubscriberExceptionHandler((exp, subscriber, subscriberMethod, args) -> {
                    LOGGER.error("process event error, subscriber:{}, method:{}, event:{}", subscriber.getClass().getName(), subscriberMethod.getName(), Arrays.toString(args), exp);
                    DefaultArbitrateEventServiceImpl.this.terminated(new TerminEventData(TerminEventData.TerminType.ROLLBACK));
                }).build();
    }

    @Override
    public void selected(ETLEventData data) {
        eventBus.post(new SelectArbitrateEvent(data));
    }

    @Override
    public void extracted(ETLEventData data) {
        eventBus.post(new ExtractArbitrateEvent(data));
    }

    @Override
    public void transformed(ETLEventData data) {
        eventBus.post(new TransformArbitrateEvent(data));
    }

    @Override
    public void loaded(ETLEventData data) {
        eventBus.post(new LoadArbitrateEvent(data));
    }

    @Override
    public void terminated(TerminEventData data) {
        eventBus.post(new TerminArbitrateEvent(data));
    }

    @Override
    public void register(Object listener) {
        eventBus.register(listener);
    }
}

package com.d8gmyself.dbsync.etl.transform;

import com.d8gmyself.core.eventbus.Subscribe;
import com.d8gmyself.dbsync.arbitrate.ArbitrateEventService;
import com.d8gmyself.dbsync.arbitrate.event.ETLEventData;
import com.d8gmyself.dbsync.arbitrate.event.ExtractArbitrateEvent;
import com.d8gmyself.dbsync.etl.commons.GlobalTask;
import com.d8gmyself.dbsync.etl.commons.model.EventData;
import com.d8gmyself.dbsync.etl.transform.transformer.Transformer;
import com.d8gmyself.dbsync.utils.LogUtils;
import com.google.common.base.Stopwatch;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by ZhangDuo on 2016-3-10 13:00.
 * <p/>
 * SETL中的T，主要用于转换原库数据为目标库所需数据，并进行数据合并
 *
 * @author ZhangDuo
 */
public class TransformTask extends GlobalTask {

    private final Transformer transformer;


    public TransformTask(ArbitrateEventService arbitrateEventService, Transformer transformer) {
        this.transformer = transformer;
        this.arbitrateEventService = arbitrateEventService;
        arbitrateEventService.register(this);

    }

    @Subscribe(async = true)
    public void doTransform(final ExtractArbitrateEvent extractArbitrateEvent) {
        final ETLEventData etlEventData = extractArbitrateEvent.getData();
        if (getProcessIdGenerator().inProcessPool(etlEventData.getProcessId())) {
            final Stopwatch stopwatch = Stopwatch.createStarted();
            LogUtils.initProcessId(etlEventData.getProcessId());
            List<EventData> eventDatas = transformer.transform(etlEventData.getDatas());
            eventDatas = transformer.merge(eventDatas);
            etlEventData.setDatas(eventDatas);
            log.info("transformed datasSize:{}, cost:{}", etlEventData.getDatas().size(), stopwatch.elapsed(TimeUnit.MILLISECONDS) + "ms");
            arbitrateEventService.transformed(etlEventData);
        }
    }

}

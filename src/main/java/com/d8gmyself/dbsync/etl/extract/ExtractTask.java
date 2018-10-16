package com.d8gmyself.dbsync.etl.extract;

import com.d8gmyself.core.eventbus.Subscribe;
import com.d8gmyself.dbsync.arbitrate.ArbitrateEventService;
import com.d8gmyself.dbsync.arbitrate.event.ETLEventData;
import com.d8gmyself.dbsync.arbitrate.event.SelectArbitrateEvent;
import com.d8gmyself.dbsync.etl.commons.GlobalTask;
import com.d8gmyself.dbsync.etl.commons.model.EventData;
import com.d8gmyself.dbsync.etl.extract.extractor.Extractor;
import com.google.common.base.Stopwatch;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by ZhangDuo on 2016-3-10 12:41.
 *
 * @author ZhangDuo
 */
public class ExtractTask extends GlobalTask {

    private final Extractor extractor;

    public ExtractTask(ArbitrateEventService arbitrateEventService, Extractor extractor) {
        this.extractor = extractor;
        this.arbitrateEventService = arbitrateEventService;
        this.arbitrateEventService.register(this);
    }


    @Subscribe(async = true)
    public void doExtract(final SelectArbitrateEvent selectEvent) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        final ETLEventData etlEventData = selectEvent.getData();
        if (processIdGenerator.inProcessPool(etlEventData.getProcessId())) {
            final List<EventData> extractedEventDatas = etlEventData.getDatas().parallelStream().filter(extractor::isInvalid).sorted(
                    (o1, o2) -> o1.getIndex() - o2.getIndex() > 0 ? 1 : o1.getIndex() - o2.getIndex() == 0 ? 0 : -1
            ).collect(Collectors.toList());
            etlEventData.setDatas(extractedEventDatas);
            log.info("extracted datasSize:{}, cost:{}ms", etlEventData.getDatas().size(), stopwatch.elapsed(TimeUnit.MILLISECONDS));
            arbitrateEventService.extracted(etlEventData);
        }
    }

}

package com.d8gmyself.dbsync.etl.load;

import com.d8gmyself.core.eventbus.Subscribe;
import com.d8gmyself.dbsync.arbitrate.ArbitrateEventService;
import com.d8gmyself.dbsync.arbitrate.event.ETLEventData;
import com.d8gmyself.dbsync.arbitrate.event.TerminEventData;
import com.d8gmyself.dbsync.arbitrate.event.TransformArbitrateEvent;
import com.d8gmyself.dbsync.etl.commons.GlobalTask;
import com.d8gmyself.dbsync.etl.commons.model.EventData;
import com.d8gmyself.dbsync.etl.load.loader.Loader;
import com.d8gmyself.dbsync.utils.LogUtils;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by ZhangDuo on 2016-3-11 9:59.
 * <p/>
 * load任务
 *
 * @author ZhangDuo
 */
public class LoadTask extends GlobalTask {


    private Loader loader;

    private PriorityBlockingQueue<ETLEventData> datas = new PriorityBlockingQueue<>(11, Comparator.comparingLong(ETLEventData::getProcessId));

    private ExecutorService loadExecutor = Executors.newFixedThreadPool(5);
    private final Thread loadThread;


    /**
     * 构造
     *
     * @param arbitrateEventService 调度服务
     */
    public LoadTask(ArbitrateEventService arbitrateEventService, Loader loader) {
        this.loader = loader;
        this.arbitrateEventService = arbitrateEventService;
        arbitrateEventService.register(this);
        LogUtils.initPipelineId(pipelineId);
        loadThread = new Thread(() -> {
            while (running && !Thread.currentThread().isInterrupted()) {
                try {
                    ETLEventData data = datas.take();
                    LoadTask.this.innerLoad(data);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    LoadTask.this.rollback();
                }
            }
        });
        loadThread.setName("pipeline=" + pipelineId + ", task=loaded");
        loadThread.start();
    }

    /**
     * 响应transform事件，transform完成后执行该方法<br />
     * 但在执行时会判断当前事件的processId是否是流程中最小的，如果是，执行load，否者重新发送transform事件
     *
     * @param transformArbitrateEvent setl流程通用数据模型
     */
    @Subscribe(async = true)
    public void doLoad(TransformArbitrateEvent transformArbitrateEvent) {
        datas.offer(transformArbitrateEvent.getData());
    }

    private synchronized void innerLoad(ETLEventData etlEventData) {
        try {
            if (processIdGenerator.inProcessPool(etlEventData.getProcessId())) {
                if (processIdGenerator.getMinProcessId().equals(etlEventData.getProcessId())) {
                    final Stopwatch stopwatch = Stopwatch.createStarted();
                    long processId = etlEventData.getProcessId();
                    LogUtils.initProcessId(processId);
                    if (CollectionUtils.isNotEmpty(etlEventData.getDatas())) {
                        int num = (int) Math.ceil(etlEventData.getDatas().size() / 500.0);
                        List<Callable<Boolean>> loadWorkers = Lists.newArrayList();
                        for (int n = 0; n < num; n++) {
                            int startIndex = n * 500;
                            int endIndex = startIndex + 500 > etlEventData.getDatas().size() ? etlEventData.getDatas().size() : startIndex + 500;
                            loadWorkers.add(new LoadWorker(processId, etlEventData.getDatas().subList(startIndex, endIndex)));
                        }
                        List<Future<Boolean>> results = loadExecutor.invokeAll(loadWorkers);
                        for (Future<Boolean> result : results) {
                            result.get();
                        }
                    }
                    log.info("loaded datasSize:{}, cost:{}ms", etlEventData.getDatas().size(), stopwatch.elapsed(TimeUnit.MILLISECONDS));
                    if (etlEventData.getDatas().size() > 0) {
                        long firstExecuteTime = etlEventData.getDatas().get(0).getExecuteTime();
                        long lastExecuteTime = etlEventData.getDatas().get(etlEventData.getDatas().size() - 1).getExecuteTime();
                        long end = System.currentTimeMillis();
                        log.info("delay:t3 - t2 = {}ms, t3 - t1 = {}ms", (end - lastExecuteTime), (end - firstExecuteTime));
                    }
                    arbitrateEventService.terminated(new TerminEventData(etlEventData.getProcessId(), etlEventData.getBatchId(),
                            TerminEventData.TerminType.NORMAL));
                } else {
                    datas.put(etlEventData);
                }
            }
        } catch (Exception e) {
            log.error("loadTask error...", e);
            rollback();
        }
    }

    /**
     * load数据
     *
     * @param eventDatas setl流程通用数据模型
     */
    private void doLoad(List<EventData> eventDatas) {
        if (CollectionUtils.isNotEmpty(eventDatas)) {
            loader.changeToSqls(eventDatas);
            loader.load(eventDatas);
        }
    }

    public void shutdown() {
        this.running = false;
        this.loadThread.interrupt();
        this.datas.clear();
    }

    private class LoadWorker implements Callable<Boolean> {

        private List<EventData> datas;
        private final long processId;

        LoadWorker(long processId, List<EventData> datas) {
            this.processId = processId;
            this.datas = datas;
        }

        @Override
        public Boolean call() {
            LogUtils.initProcessId(processId);
            LoadTask.this.doLoad(datas);
            return Boolean.TRUE;
        }

    }

}

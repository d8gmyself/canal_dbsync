package com.d8gmyself.dbsync.etl.select;

import com.alibaba.otter.canal.common.utils.BooleanMutex;
import com.d8gmyself.core.eventbus.Subscribe;
import com.d8gmyself.dbsync.arbitrate.ArbitrateEventService;
import com.d8gmyself.dbsync.arbitrate.event.ETLEventData;
import com.d8gmyself.dbsync.arbitrate.event.TerminArbitrateEvent;
import com.d8gmyself.dbsync.arbitrate.event.TerminEventData;
import com.d8gmyself.dbsync.etl.commons.GlobalTask;
import com.d8gmyself.dbsync.etl.select.selector.Message;
import com.d8gmyself.dbsync.etl.select.selector.Selector;
import com.d8gmyself.dbsync.utils.LogUtils;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by ZhangDuo on 2016-3-10 9:56.
 * <p/>
 * SETL中的S，主要用于获取canal中的数据
 *
 * @author ZhangDuo
 */
public class SelectTask extends GlobalTask {

    /**
     * 用于控制是否可执行查询
     */
    private final  BooleanMutex canSelector = new BooleanMutex(true);
    /**
     * 用于获取canal的Message
     */
    private final Selector selector;
    /**
     * 用于执行线程
     */
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    /**
     * 用于控制setl流程的最大并行度，默认为5
     */
    private BlockingQueue<Long> batchBuffer;
    /**
     * 查询版本，保证当前拿到的数据是期望的数据，而不是已被回滚的数据
     */
    private AtomicLong rversion = new AtomicLong(0);

    public SelectTask(ArbitrateEventService arbitrateEventService, Selector selector) {
        this.selector = selector;
        this.arbitrateEventService = arbitrateEventService;
        arbitrateEventService.register(this);
    }

    /**
     * 启动流程
     */
    public void startup() {
        LogUtils.initPipelineId(pipelineId);
        batchBuffer = new LinkedBlockingQueue<>(pipeline.getParallelism());
        selector.start();
        processSelect();
    }

    /**
     * 查询程序，用于查询canal的Message并发送到下一环节
     */
    private void processSelect() {
        executor.execute(() -> {
            while (running) {
                try {
                    long startVersion = rversion.get();
                    canSelector.get();
                    /*
                     * case startVersion-get
                     *          reversion++
                     *      canSelector.get()
                     * 此时虽然startVersion不相等，但不会“脏读”，为了兼容下面的逻辑
                     * 让其等待rollback完成后重新走获取startVersion
                     */
                    if (startVersion != rversion.get()) {
                        canSelector.get();
                        continue;
                    }
                    final Message message = selector.selector();
                    final Long processId = processIdGenerator.getProcessId();
                    batchBuffer.put(message.getId());
                    /*
                     * 判断本次selector以及batchBuffer.put、processId生成过程中有无rollback
                     * 若有且rversion被更改，则rollback && clear && 等待rollback完成continue
                     * 若rversion还未来得及被更改，该数据也会由于rollback的clear而被后续流程抛弃
                     */
                    if (startVersion != rversion.get()) {
                        selector.rollback();
                        processIdGenerator.clearCurrentProcessIds();
                        batchBuffer.clear();
                        canSelector.get();
                        continue;
                    }
                    executorService.execute(() -> {
                        LogUtils.initProcessId(processId);
                        final ETLEventData etlEventData = new ETLEventData();
                        etlEventData.setProcessId(processId);
                        etlEventData.setBatchId(message.getId());
                        etlEventData.setPipelineId(pipelineId);
                        etlEventData.setDatas(message.getDatas());
                        log.info("selected datasSize:{}", etlEventData.getDatas().size());
                        arbitrateEventService.selected(etlEventData);
                    });
                } catch (Exception e) {
                    log.error("selectTask error...", e);
                    rollback();
                }

            }
        });
    }

    /**
     * 响应结束信号，执行ack或rollback
     *
     * @param terminArbitrateEvent 结束事件
     */
    @Subscribe(async = true)
    public void processTermin(TerminArbitrateEvent terminArbitrateEvent) {
        TerminEventData terminEventData = terminArbitrateEvent.getData();
        if (terminEventData.getTerminType().isRollback()) { //回滚操作
            canSelector.set(false);
            rversion.incrementAndGet();
            processIdGenerator.clearCurrentProcessIds();
            batchBuffer.clear();
            selector.rollback();
            log.warn("rollback pipeline:{}", pipelineId);
            canSelector.set(true);
        } else if (terminEventData.getTerminType().isNormal()) { //提交操作
            try {
                if (terminEventData.getBatchId().equals(batchBuffer.poll()) && getProcessIdGenerator().removeProcessId(terminEventData.getProcessId())) {
                    selector.ack(terminEventData.getBatchId());
                    log.info("ack process:{}, pipeline:{}", terminEventData.getProcessId(), pipelineId);
                } else {
                    log.error("arbitrate error...");
                    arbitrateEventService.terminated(new TerminEventData(terminEventData.getProcessId(), terminEventData.getBatchId(), TerminEventData.TerminType.ROLLBACK));
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                arbitrateEventService.terminated(new TerminEventData(terminEventData.getProcessId(), terminEventData.getBatchId(), TerminEventData.TerminType.ROLLBACK));
            }
        }
    }

    /**
     * 关闭流程
     */
    public void shutdown() {
        canSelector.set(false);
        rversion.incrementAndGet();
        processIdGenerator.clearCurrentProcessIds();
        batchBuffer.clear();
        selector.rollback();
        selector.stop();
        this.running = false;
        executor.shutdownNow();
        executorService.shutdownNow();
    }

}

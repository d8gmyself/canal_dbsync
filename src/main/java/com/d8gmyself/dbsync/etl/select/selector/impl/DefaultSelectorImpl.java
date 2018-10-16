package com.d8gmyself.dbsync.etl.select.selector.impl;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.d8gmyself.dbsync.commons.model.Pipeline;
import com.d8gmyself.dbsync.etl.commons.model.EventData;
import com.d8gmyself.dbsync.etl.select.selector.Message;
import com.d8gmyself.dbsync.etl.select.selector.MessageParser;
import com.d8gmyself.dbsync.etl.select.selector.Selector;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.locks.LockSupport;

/**
 * Created by ZhangDuo on 2016-3-10 10:11.
 * <p/>
 * 默认的{@link Selector}实现
 *
 * @author ZhangDuo
 */
public class DefaultSelectorImpl implements Selector {

    private static final Logger log = LoggerFactory.getLogger(DefaultSelectorImpl.class);

    private static final int maxEmptyTimes = 10;

    private volatile boolean running = false;
    private Long pipelineId;
    private Pipeline pipeline;
    private CanalConnector canalConnector;
    private int batchSize = 10000;

    public DefaultSelectorImpl(Pipeline pipeline) {
        Preconditions.checkNotNull(pipeline, "pipeline can not be null...");
        this.pipelineId = pipeline.getId();
        this.pipeline = pipeline;
    }

    @Override
    public void start() {
        Preconditions.checkNotNull(pipeline, "pipeline can not be null...");
        if (running) {
            return;
        }
        canalConnector = CanalConnectors.newClusterConnector(pipeline.getZkServers(), pipeline.getDestination(), pipeline.getCanalUsername(),
                pipeline.getCanalPassword());
        canalConnector.connect();
        canalConnector.subscribe();
        batchSize = pipeline.getBatchSize();
        running = true;
        log.info("selector started...pipeline:{}", pipelineId);
    }

    @Override
    public boolean isStart() {
        return running;
    }

    @Override
    public void stop() {
        if (!running) {
            return;
        }
        running = false;
        canalConnector.unsubscribe();
        canalConnector.disconnect();
        canalConnector = null;
    }

    @Override
    public Message selector() throws InterruptedException {
        int emptyTimes = 0;
        com.alibaba.otter.canal.protocol.Message message = null;
        while (running) {
            message = canalConnector.getWithoutAck(batchSize);
            if (message == null || message.getId() == -1L) {
                applyWait(emptyTimes++);
            } else {
                break;
            }
        }
        if (!running) {
            throw new InterruptedException("selector stoped!");
        }
        Preconditions.checkNotNull(message, "message can not be null");
        List<EventData> datas = MessageParser.parse(pipelineId, message.getEntries());
        return new Message(message.getId(), datas);
    }

    @Override
    public void rollback(Long batchId) {
        canalConnector.rollback(batchId);
    }

    @Override
    public void rollback() {
        canalConnector.rollback();
    }

    @Override
    public void ack(Long batchId) {
        canalConnector.ack(batchId);
    }

    // 处理无数据的情况，避免空循环挂死
    private void applyWait(int emptyTimes) {
        int newEmptyTimes = emptyTimes > maxEmptyTimes ? maxEmptyTimes : emptyTimes;
        if (emptyTimes <= 3) { // 3次以内
            Thread.yield();
        } else { // 超过3次，最多只sleep 10ms
            LockSupport.parkNanos(1000 * 1000L * newEmptyTimes);
        }
    }

}

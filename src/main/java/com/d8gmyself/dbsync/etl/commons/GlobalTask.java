package com.d8gmyself.dbsync.etl.commons;

import com.d8gmyself.dbsync.arbitrate.ArbitrateEventService;
import com.d8gmyself.dbsync.arbitrate.ProcessIdGenerator;
import com.d8gmyself.dbsync.arbitrate.event.TerminEventData;
import com.d8gmyself.dbsync.commons.model.Pipeline;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;

/**
 * Created by ZhangDuo on 2016-3-10 13:01.
 * <p>
 * 公共Task模型
 *
 * @author ZhangDuo
 */
public abstract class GlobalTask {

    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * 渠道id
     */
    protected Long pipelineId;
    /**
     * 渠道
     */
    protected Pipeline pipeline;
    /**
     * 是否运行
     */
    protected volatile boolean running = true;
    /**
     * 调度服务,一套完整的setl流程要持有同一个调度服务
     */
    protected ArbitrateEventService arbitrateEventService;
    /**
     * processId生成器，一套完整的setl流程要持有同一个processId生成器
     */
    protected ProcessIdGenerator processIdGenerator;
    /**
     * 线程池，一套完整的setl流程，最好共用一个线程池，由并发数来决定线程池大小
     */
    protected ExecutorService executorService;

    public Long getPipelineId() {
        return pipelineId;
    }

    public void setPipelineId(Long pipelineId) {
        this.pipelineId = pipelineId;
    }

    public Pipeline getPipeline() {
        return pipeline;
    }

    public void setPipeline(Pipeline pipeline) {
        this.pipeline = pipeline;
    }

    public ProcessIdGenerator getProcessIdGenerator() {
        return processIdGenerator;
    }

    public void setProcessIdGenerator(ProcessIdGenerator processIdGenerator) {
        this.processIdGenerator = processIdGenerator;
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    public void rollback(Long processId, Long batchId) {
        arbitrateEventService.terminated(new TerminEventData(processId, batchId, TerminEventData.TerminType.ROLLBACK));
    }

    public void rollback() {
        arbitrateEventService.terminated(new TerminEventData(TerminEventData.TerminType.ROLLBACK));
    }

}

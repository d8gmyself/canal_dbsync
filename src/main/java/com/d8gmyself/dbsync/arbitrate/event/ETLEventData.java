package com.d8gmyself.dbsync.arbitrate.event;

import com.d8gmyself.dbsync.etl.commons.model.EventData;

import java.util.List;

/**
 * Created by ZhangDuo on 2016-3-10 12:03.
 * <p>
 * setl流程流转时使用的通用数据模型
 *
 * @author ZhangDuo
 */
public class ETLEventData {

    /**
     * 渠道id，主要为了获取渠道数据同步配置
     */
    private Long pipelineId;
    /**
     * 同步进程id
     */
    private Long processId;
    /**
     * binlog数据批次id
     */
    private Long batchId;
    /**
     * 数据信息
     */
    private List<EventData> datas;

    public Long getProcessId() {
        return processId;
    }

    public void setProcessId(Long processId) {
        this.processId = processId;
    }

    public Long getBatchId() {
        return batchId;
    }

    public void setBatchId(Long batchId) {
        this.batchId = batchId;
    }

    public List<EventData> getDatas() {
        return datas;
    }

    public void setDatas(List<EventData> datas) {
        this.datas = datas;
    }

    public Long getPipelineId() {
        return pipelineId;
    }

    public void setPipelineId(Long pipelineId) {
        this.pipelineId = pipelineId;
    }
}

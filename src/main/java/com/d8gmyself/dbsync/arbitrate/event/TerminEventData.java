package com.d8gmyself.dbsync.arbitrate.event;

/**
 * Created by ZhangDuo on 2016-3-11 15:24.
 * <p>
 * SETL流程完结消息
 *
 * @author ZhangDuo
 */
public class TerminEventData {
    /**
     * processId
     */
    private Long processId;
    /**
     * batchId
     */
    private Long batchId;
    /**
     * 消息类型
     */
    private TerminType terminType;

    public TerminEventData(TerminType terminType) {
        this.processId = -1L;
        this.batchId = -1L;
        this.terminType = terminType;
    }

    public TerminEventData(Long processId, Long batchId, TerminType terminType) {
        this.processId = processId;
        this.batchId = batchId;
        this.terminType = terminType;
    }

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

    public TerminType getTerminType() {
        return terminType;
    }

    public void setTerminType(TerminType terminType) {
        this.terminType = terminType;
    }

    public enum TerminType {

        /**
         * 正常结束，ack
         */
        NORMAL,
        /**
         * 回滚数据
         */
        ROLLBACK;

        public boolean isNormal() {
            return this.equals(NORMAL);
        }

        public boolean isRollback() {
            return this.equals(ROLLBACK);
        }
    }

}

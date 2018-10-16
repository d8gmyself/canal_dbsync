package com.d8gmyself.dbsync.etl.select.selector;

/**
 * Created by ZhangDuo on 2016-3-10 9:56.
 * <p>
 * binlog查询器
 *
 * @author ZhangDuo
 */
public interface Selector {

    /**
     * 启动查询
     */
    void start();

    /**
     * 是否启动
     *
     * @return 启动-true
     */
    boolean isStart();

    /**
     * 停止查询
     */
    void stop();

    /**
     * 获取一批待处理的对象
     *
     * @return 待处理的对象
     * @throws InterruptedException
     */
    Message selector() throws InterruptedException;

    /**
     * rollback指定批次
     *
     * @param batchId 批次号
     */
    void rollback(Long batchId);

    /**
     * rollback未被ack的所有批次
     */
    void rollback();

    /**
     * 反馈一批数据处理完成
     *
     * @param batchId 批次号
     */
    void ack(Long batchId);

}

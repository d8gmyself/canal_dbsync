package com.d8gmyself.dbsync.commons.model;

import java.util.List;
import java.util.Map;

/**
 * Created by ZhangDuo on 2016-3-10 10:31.
 * <p>
 * 渠道信息
 *
 * @author ZhangDuo
 */
public class Pipeline {

    /**
     * 渠道id
     */
    private Long id;
    /**
     * 要连接的canal的destination
     */
    private String destination;
    /**
     * 要连接的zk集群
     */
    private String zkServers;
    /**
     * canal用户名
     */
    private String canalUsername;
    /**
     * canal密码
     */
    private String canalPassword;
    /**
     * 要同步的表映射，key为DataMediaPair的srcSchema.srcTanleName
     */
    private Map<String, List<DataMediaPair>> dataMediaPairs;
    /**
     * 并行度
     */
    private int parallelism;
    /**
     * 每次获取binlog的batchSize大小
     */
    private int batchSize;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getZkServers() {
        return zkServers;
    }

    public void setZkServers(String zkServers) {
        this.zkServers = zkServers;
    }

    public String getCanalUsername() {
        return canalUsername;
    }

    public void setCanalUsername(String canalUsername) {
        this.canalUsername = canalUsername;
    }

    public String getCanalPassword() {
        return canalPassword;
    }

    public void setCanalPassword(String canalPassword) {
        this.canalPassword = canalPassword;
    }

    public Map<String, List<DataMediaPair>> getDataMediaPairs() {
        return dataMediaPairs;
    }

    public void setDataMediaPairs(Map<String, List<DataMediaPair>> dataMediaPairs) {
        this.dataMediaPairs = dataMediaPairs;
    }

    public int getParallelism() {
        return parallelism;
    }

    public void setParallelism(int parallelism) {
        this.parallelism = parallelism;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }
}

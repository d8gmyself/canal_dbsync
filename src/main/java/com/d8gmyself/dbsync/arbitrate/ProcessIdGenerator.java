package com.d8gmyself.dbsync.arbitrate;

import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by ZhangDuo on 2016-3-11 9:45.
 * <p>
 * 生成 && 控制processId
 *
 * @author ZhangDuo
 */
public class ProcessIdGenerator {

    /**
     * processId，通过原子递增来生成
     */
    private AtomicLong atomicMaxProcessId = new AtomicLong(0);
    /**
     * 当前etl流程中存活的所有processId
     */
    private List<Long> currentProcessIds = Lists.newArrayList();

    /**
     * 获取processId
     *
     * @return processId
     */
    public synchronized Long getProcessId() {
        Long processId = this.atomicMaxProcessId.incrementAndGet();
        currentProcessIds.add(processId);
        return processId;
    }

    /**
     * 清空当前setl流程中的所有processId
     */
    public synchronized void clearCurrentProcessIds() {
        this.currentProcessIds.clear();
    }

    /**
     * 获取当前setl流程中最小的processId <br />
     * 若当前当前流程中没有process存活，返回-1
     *
     * @return processId
     */
    public synchronized Long getMinProcessId() {
        if (CollectionUtils.isNotEmpty(currentProcessIds)) {
            return Collections.min(currentProcessIds);
        } else {
            return -1L;
        }
    }

    /**
     * 从当前selt流程中删除指定processId
     *
     * @param processId 要删除的processId
     * @return 是否删除成功
     */
    public synchronized boolean removeProcessId(Long processId) {
        return currentProcessIds.remove(processId);
    }

    /**
     * 判断给定ProcessId是否包含在当前所有存活的processId集合之中
     *
     * @param processId processId
     * @return 是否包含
     */
    public synchronized boolean inProcessPool(Long processId) {
        return currentProcessIds.contains(processId);
    }

}

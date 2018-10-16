package com.d8gmyself.dbsync.utils;

import org.slf4j.MDC;

/**
 * Created by ZhangDuo on 2016-3-16 16:51.
 * <p>
 * 日志上下文辅助
 *
 * @author ZhangDuo
 */
public class LogUtils {

    private LogUtils() {}

    public static void initPipelineId(Long pipelineId) {
        if (pipelineId != null) {
            MDC.put("pipelineId", pipelineId.toString());
        }
    }

    public static void initProcessId(Long processId) {
        if (processId != null) {
            MDC.put("processId", processId.toString());
        }
    }

}

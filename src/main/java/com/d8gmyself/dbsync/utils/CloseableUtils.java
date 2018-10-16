package com.d8gmyself.dbsync.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by ZhangDuo on 2016-3-17 9:24.
 * <p/>
 * Closeable辅助类
 *
 * @author ZhangDuo
 */
public class CloseableUtils {

    private static final Logger log = LoggerFactory.getLogger(CloseableUtils.class);

    private CloseableUtils() {}

    /**
     * 关闭资源，JDK1.7加入了AutoCloseable接口
     *
     * @param obj 要关闭的资源
     */
    public static void close(AutoCloseable obj) {
        if (obj != null) {
            try {
                obj.close();
            } catch (Exception e) {
                log.error("IOException should not have been thrown.", e);
            }
        }

    }

}

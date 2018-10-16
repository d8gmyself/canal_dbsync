package com.d8gmyself.dbsync.utils;

import com.d8gmyself.dbsync.commons.config.PipelineConfig;
import com.d8gmyself.dbsync.commons.model.Pipeline;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.concurrent.ExecutionException;

/**
 * Created by ZhangDuo on 2016-3-10 14:16.
 * <p>
 * 渠道工厂，用于获取渠道配置信息
 *
 * @author ZhangDuo
 */
public class PipelineFactory {

    /**
     * 用于缓存渠道配置信息
     */
    private static final LoadingCache<Long, Pipeline> pipelineCache = CacheBuilder.<Long, Pipeline>newBuilder().build(new CacheLoader<Long, Pipeline>() {
        @Override
        public Pipeline load(Long key) throws Exception {
            return PipelineConfig.getPipelineById(key);
        }
    });

    private PipelineFactory() {}

    /**
     * 获取指定pipelineId的配置信息
     *
     * @param pipelineId 指定的pipelineId
     * @return 渠道配置信息
     */
    public static Pipeline buildPipeline(Long pipelineId) {
        try {
            return pipelineCache.get(pipelineId);
        } catch (ExecutionException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

}

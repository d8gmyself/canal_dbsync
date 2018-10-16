package com.d8gmyself.dbsync.etl.select.selector;

import com.d8gmyself.dbsync.commons.config.PipelineConfig;
import com.d8gmyself.dbsync.commons.model.Pipeline;
import com.d8gmyself.dbsync.etl.select.exception.SelectException;
import com.d8gmyself.dbsync.etl.select.selector.impl.DefaultSelectorImpl;
import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.concurrent.ExecutionException;

/**
 * Created by ZhangDuo on 2016-3-10 9:57.
 * <p>
 * {@link Selector}工厂，用于获取不同配置的{@link Selector}
 *
 * @author ZhangDuo
 */
public class SelectorFactory {

    private SelectorFactory() {
    }

    /**
     * Selector缓存
     */
    private static final LoadingCache<Long, Selector> selectorCache = CacheBuilder.<Long, Selector>newBuilder().build(new CacheLoader<Long, Selector>() {
        @Override
        public Selector load(Long key) throws Exception {
            Pipeline pipeline = PipelineConfig.getPipelineById(key);
            return new DefaultSelectorImpl(pipeline);
        }
    });


    /**
     * 通过pipelineId获取相应的{@link Selector}
     *
     * @param pipelineId pipelineId
     * @return 相应的selector
     */
    public static Selector getSelector(Long pipelineId) {
        try {
            return selectorCache.get(pipelineId);
        } catch (ExecutionException e) {
            throw new SelectException(e.getMessage(), e);
        }
    }

    /**
     * 通过pipeline获取相应的{@link Selector}
     *
     * @param pipeline pipeline
     * @return 相应的selector
     */
    public static Selector getSelector(Pipeline pipeline) {
        Preconditions.checkNotNull(pipeline, "pipeline can not be null...");
        return getSelector(pipeline.getId());
    }

}

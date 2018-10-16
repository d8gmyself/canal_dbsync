package com.d8gmyself.dbsync.utils;

import com.d8gmyself.dbsync.commons.config.PipelineConfig;
import com.d8gmyself.dbsync.commons.model.Pipeline;
import com.d8gmyself.dbsync.etl.SETLInstanceWithSpring;
import com.google.common.collect.Lists;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.List;

/**
 * Created by ZhangDuo on 2016-3-15 14:19.
 * <p>
 * ServletContext监听，用于在Tomcat启动时初始化并启动SETL流程实例
 *
 * @author ZhangDuo
 */
public class StartListener implements ServletContextListener {

    private List<SETLInstanceWithSpring> instances = Lists.newArrayList();

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        for (final Pipeline pipeline : PipelineConfig.getPipelines().values()) {
            new Thread(() -> {
                Thread.currentThread().setName("StartSETL");
                SETLInstanceWithSpring instance = (SETLInstanceWithSpring) BeanUtils.getBean(pipeline.getId(), "instance");
                instance.start();
                instances.add(instance);
            }).start();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        for (SETLInstanceWithSpring instance : instances) {
            instance.stop();
        }
        BeanUtils.close();
    }
}

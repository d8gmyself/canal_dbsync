package com.d8gmyself.dbsync.utils;

import com.google.common.collect.Maps;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Map;

/**
 * Created by ZhangDuo on 2016-3-8 10:18.
 * <p>
 * Spring BeanFactory辅助类
 *
 * @author ZhangDuo
 */
public class BeanUtils {

    /**
     * 同于存放多个BeanFactory
     */
    private static final Map<Long, BeanFactory> factories = Maps.newConcurrentMap();
    /**
     * 基础的BeanFactory
     */
    private static final BeanFactory beanFactory = new ClassPathXmlApplicationContext("classpath:spring/root-context.xml");

    private BeanUtils() {

    }

    /**
     * 通过beanName获取对象
     *
     * @param beanName beanName
     * @return 对应对象
     */
    public static Object getBean(String beanName) {
        return beanFactory.getBean(beanName);
    }

    /**
     * 通过beanName和类型获取对象
     *
     * @param beanName beanName
     * @param clazz    类型
     * @return 对应对象
     */
    public static Object getBean(String beanName, Class clazz) {
        return beanFactory.getBean(beanName, clazz);
    }

    /**
     * 销毁所有BeanFactory
     */
    public static void close() {
        for (BeanFactory factory : factories.values()) {
            ((ClassPathXmlApplicationContext) factory).close();
        }
        ((ClassPathXmlApplicationContext) beanFactory).close();
    }

    /**
     * 通过pipelineId、beanName获取对应对象
     *
     * @param pipelineId pipelineId
     * @param beanName   beanName
     * @return 对应对象
     */
    public static Object getBean(Long pipelineId, String beanName) {
        if (factories.get(pipelineId) == null) {
            synchronized (BeanUtils.class) {
                if (factories.get(pipelineId) != null) {
                    return factories.get(pipelineId).getBean(beanName);
                }
                BeanFactory beanFactory = new ClassPathXmlApplicationContext("classpath:spring/root-context-" + pipelineId + ".xml");
                factories.put(pipelineId, beanFactory);
            }
        }
        return factories.get(pipelineId).getBean(beanName);
    }

}

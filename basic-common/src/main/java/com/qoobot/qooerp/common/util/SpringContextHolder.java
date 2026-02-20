package com.qoobot.qooerp.common.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

/**
 * Spring上下文持有者
 * 用于在非Spring管理的类中获取Spring容器中的Bean
 *
 * @author QooERP Team
 * @version 1.0.0
 */
@Slf4j
@Component
public class SpringContextHolder implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        SpringContextHolder.applicationContext = applicationContext;
        log.debug("SpringContextHolder initialized, ApplicationContext: {}", applicationContext.getClass().getName());
    }

    /**
     * 获取ApplicationContext
     *
     * @return ApplicationContext
     */
    public static ApplicationContext getApplicationContext() {
        assertApplicationContext();
        return applicationContext;
    }

    /**
     * 根据类型获取Bean
     *
     * @param clazz Bean类型
     * @param <T>   Bean类型
     * @return Bean实例
     */
    public static <T> T getBean(Class<T> clazz) {
        assertApplicationContext();
        return applicationContext.getBean(clazz);
    }

    /**
     * 根据名称和类型获取Bean
     *
     * @param name  Bean名称
     * @param clazz Bean类型
     * @param <T>   Bean类型
     * @return Bean实例
     */
    public static <T> T getBean(String name, Class<T> clazz) {
        assertApplicationContext();
        return applicationContext.getBean(name, clazz);
    }

    /**
     * 根据名称获取Bean
     *
     * @param name Bean名称
     * @return Bean实例
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name) {
        assertApplicationContext();
        return (T) applicationContext.getBean(name);
    }

    /**
     * 判断是否包含指定名称的Bean
     *
     * @param name Bean名称
     * @return 是否包含
     */
    public static boolean containsBean(String name) {
        assertApplicationContext();
        return applicationContext.containsBean(name);
    }

    /**
     * 判断指定Bean是否单例
     *
     * @param name Bean名称
     * @return 是否单例
     */
    public static boolean isSingleton(String name) {
        assertApplicationContext();
        return applicationContext.isSingleton(name);
    }

    /**
     * 获取Bean的类型
     *
     * @param name Bean名称
     * @return Bean类型
     */
    public static Class<?> getType(String name) {
        assertApplicationContext();
        return applicationContext.getType(name);
    }

    /**
     * 发布事件
     *
     * @param event 事件对象
     */
    public static void publishEvent(Object event) {
        assertApplicationContext();
        applicationContext.publishEvent(event);
    }

    /**
     * 断言ApplicationContext不为空
     */
    private static void assertApplicationContext() {
        if (applicationContext == null) {
            throw new IllegalStateException("ApplicationContext is not initialized, please check SpringContextHolder configuration");
        }
    }

    /**
     * 清除ApplicationContext引用
     */
    public static void clear() {
        log.debug("Clearing SpringContextHolder applicationContext");
        applicationContext = null;
    }
}

package com.qoobot.qooerp.common.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Bean工具类
 * 提供对象属性复制、集合转换等功能
 *
 * @author QooERP Team
 * @since 2026-02-17
 */
@Component
public class BeanUtils implements ApplicationContextAware {

    private static ApplicationContext applicationContext;


    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        BeanUtils.applicationContext = context;
    }

    /**
     * 获取Spring容器中的Bean
     *
     * @param clazz Bean类型
     * @param <T>   泛型
     * @return Bean实例
     */
    public static <T> T getBean(Class<T> clazz) {
        return applicationContext.getBean(clazz);
    }

    /**
     * 获取Spring容器中的Bean
     *
     * @param name Bean名称
     * @param <T>  泛型
     * @return Bean实例
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name) {
        return (T) applicationContext.getBean(name);
    }

    /**
     * 复制属性
     *
     * @param source 源对象
     * @param target 目标对象
     */
    public static void copyProperties(Object source, Object target) {
        if (source == null || target == null) {
            return;
        }
        BeanUtils.copyProperties(source, target);
    }

    /**
     * 复制属性（忽略某些属性）
     *
     * @param source           源对象
     * @param target           目标对象
     * @param ignoreProperties 忽略的属性名
     */
    public static void copyProperties(Object source, Object target, String... ignoreProperties) {
        if (source == null || target == null) {
            return;
        }
        BeanUtils.copyProperties(source, target, ignoreProperties);
    }

    /**
     * 复制对象
     *
     * @param source      源对象
     * @param targetClass 目标类
     * @param <T>        泛型
     * @return 目标对象
     */
    public static <T> T copyBean(Object source, Class<T> targetClass) {
        if (source == null) {
            return null;
        }
        try {
            T target = targetClass.getDeclaredConstructor().newInstance();
            BeanUtils.copyProperties(source, target);
            return target;
        } catch (Exception e) {
            throw new RuntimeException("Bean copy failed", e);
        }
    }

    /**
     * 复制对象（忽略某些属性）
     *
     * @param source           源对象
     * @param targetClass       目标类
     * @param ignoreProperties  忽略的属性名
     * @param <T>              泛型
     * @return 目标对象
     */
    public static <T> T copyBean(Object source, Class<T> targetClass, String... ignoreProperties) {
        if (source == null) {
            return null;
        }
        try {
            T target = targetClass.getDeclaredConstructor().newInstance();
            BeanUtils.copyProperties(source, target, ignoreProperties);
            return target;
        } catch (Exception e) {
            throw new RuntimeException("Bean copy failed", e);
        }
    }

    /**
     * 复制集合
     *
     * @param sourceList 源集合
     * @param targetClass 目标类
     * @param <S>         源泛型
     * @param <T>         目标泛型
     * @return 目标集合
     */
    public static <S, T> List<T> copyList(List<S> sourceList, Class<T> targetClass) {
        if (sourceList == null || sourceList.isEmpty()) {
            return new ArrayList<>();
        }
        return sourceList.stream()
                .map(source -> copyBean(source, targetClass))
                .collect(Collectors.toList());
    }

    /**
     * 复制集合（忽略某些属性）
     *
     * @param sourceList       源集合
     * @param targetClass       目标类
     * @param ignoreProperties  忽略的属性名
     * @param <S>              源泛型
     * @param <T>              目标泛型
     * @return 目标集合
     */
    public static <S, T> List<T> copyList(List<S> sourceList, Class<T> targetClass, String... ignoreProperties) {
        if (sourceList == null || sourceList.isEmpty()) {
            return new ArrayList<>();
        }
        return sourceList.stream()
                .map(source -> copyBean(source, targetClass, ignoreProperties))
                .collect(Collectors.toList());
    }

    /**
     * 判断对象是否为空
     *
     * @param obj 对象
     * @return 是否为空
     */
    public static boolean isEmpty(Object obj) {
        if (obj == null) {
            return true;
        }
        if (obj instanceof String) {
            return ((String) obj).trim().isEmpty();
        }
        if (obj instanceof Iterable) {
            return !((Iterable<?>) obj).iterator().hasNext();
        }
        return false;
    }

    /**
     * 判断对象是否不为空
     *
     * @param obj 对象
     * @return 是否不为空
     */
    public static boolean isNotEmpty(Object obj) {
        return !isEmpty(obj);
    }
}

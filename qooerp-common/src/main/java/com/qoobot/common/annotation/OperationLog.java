package com.qoobot.common.annotation;

import com.qoobot.common.enums.LogType;

import java.lang.annotation.*;

/**
 * 操作日志注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperationLog {

    /**
     * 模块名
     */
    String module();

    /**
     * 操作名称
     */
    String operation();

    /**
     * 描述
     */
    String description() default "";

    /**
     * 日志类型
     */
    LogType type() default LogType.OTHER;
}

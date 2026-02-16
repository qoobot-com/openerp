package com.qoobot.common.annotation;

import com.qoobot.common.enums.LogicalType;

import java.lang.annotation.*;

/**
 * 权限校验注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequirePermission {

    /**
     * 权限标识，支持多个，逗号分隔
     */
    String value();

    /**
     * 逻辑类型
     */
    LogicalType logical() default LogicalType.AND;
}

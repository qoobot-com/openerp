package com.qoobot.qooerp.common.annotation;

import java.lang.annotation.*;

/**
 * 数据权限注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataScope {

    /**
     * 数据权限列名
     */
    String column();

    /**
     * 表别名
     */
    String alias() default "";
}

package com.qoobot.qooerp.permission.annotation;

import java.lang.annotation.*;

/**
 * 数据权限注解
 * 用于标记需要进行数据权限过滤的方法
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataScope {

    /**
     * 表别名（SQL查询中的表别名）
     * 例如：SELECT * FROM user u WHERE ...，则表别名为 "u"
     */
    String alias() default "";

    /**
     * 部门字段名（默认为 dept_id）
     */
    String deptField() default "dept_id";

    /**
     * 用户字段名（默认为 create_by）
     */
    String userField() default "create_by";

    /**
     * 是否启用数据权限过滤
     */
    boolean enabled() default true;
}

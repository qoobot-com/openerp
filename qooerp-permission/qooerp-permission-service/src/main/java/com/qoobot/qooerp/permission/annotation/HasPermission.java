package com.qoobot.qooerp.permission.annotation;

import java.lang.annotation.*;

/**
 * 权限验证注解（支持RBAC和ABAC）
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface HasPermission {

    /**
     * 权限标识（RBAC）
     */
    String value() default "";

    /**
     * 权限标识数组（RBAC）
     */
    String[] permissions() default {};

    /**
     * 逻辑关系：AND-需要所有权限 OR-任一权限即可
     */
    Logical logical() default Logical.AND;

    /**
     * 资源类型（ABAC）
     */
    String resourceType() default "";

    /**
     * 操作类型（ABAC）
     */
    String operationType() default "";

    /**
     * 是否跳过权限检查（用于调试）
     */
    boolean skip() default false;

    /**
     * 逻辑关系枚举
     */
    enum Logical {
        AND, OR
    }
}

package com.qoobot.qooerp.permission.constants;

/**
 * 权限模块常量
 */
public class PermissionConstants {

    /**
     * 缓存key前缀
     */
    public static final String CACHE_PREFIX = "permission:";

    /**
     * 角色缓存key
     */
    public static final String CACHE_ROLE = CACHE_PREFIX + "role:";

    /**
     * 菜单缓存key
     */
    public static final String CACHE_MENU = CACHE_PREFIX + "menu:";

    /**
     * 用户角色缓存key
     */
    public static final String CACHE_USER_ROLE = CACHE_PREFIX + "user:role:";

    /**
     * 角色菜单缓存key
     */
    public static final String CACHE_ROLE_MENU = CACHE_PREFIX + "role:menu:";

    /**
     * 用户菜单缓存key
     */
    public static final String CACHE_USER_MENU = CACHE_PREFIX + "user:menu:";

    /**
     * 默认租户ID
     */
    public static final Long DEFAULT_TENANT_ID = 0L;

    /**
     * 超级管理员角色编码
     */
    public static final String ADMIN_ROLE_CODE = "ADMIN";

    /**
     * 根节点ID
     */
    public static final Long ROOT_ID = 0L;
}

package com.qoobot.common.constant;

/**
 * 缓存常量
 */
public class CacheConstant {

    /**
     * 缓存命名空间
     */
    public static final String CACHE_NAMESPACE = "qooerp:";

    /**
     * 用户信息缓存
     */
    public static final String USER_INFO_CACHE = CACHE_NAMESPACE + "user:info:";

    /**
     * 用户权限缓存
     */
    public static final String USER_PERMISSION_CACHE = CACHE_NAMESPACE + "user:permission:";

    /**
     * 用户角色缓存
     */
    public static final String USER_ROLE_CACHE = CACHE_NAMESPACE + "user:role:";

    /**
     * 字典缓存
     */
    public static final String DICT_CACHE = CACHE_NAMESPACE + "dict:";

    /**
     * 配置缓存
     */
    public static final String CONFIG_CACHE = CACHE_NAMESPACE + "config:";

    /**
     * 部门缓存
     */
    public static final String DEPT_CACHE = CACHE_NAMESPACE + "dept:";

    /**
     * 租户信息缓存
     */
    public static final String TENANT_CACHE = CACHE_NAMESPACE + "tenant:";

    /**
     * 分布式锁前缀
     */
    public static final String LOCK_PREFIX = CACHE_NAMESPACE + "lock:";

    /**
     * 限流器前缀
     */
    public static final String RATE_LIMITER_PREFIX = CACHE_NAMESPACE + "rate:";

    /**
     * 验证码前缀
     */
    public static final String CAPTCHA_PREFIX = CACHE_NAMESPACE + "captcha:";
}

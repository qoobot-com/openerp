package com.qoobot.qooerp.auth.constants;

/**
 * 令牌常量
 */
public class TokenConstant {

    /**
     * Bearer令牌前缀
     */
    public static final String BEARER_PREFIX = "Bearer ";

    /**
     * JWT用户ID声明
     */
    public static final String CLAIM_USER_ID = "userId";

    /**
     * JWT用户名声明
     */
    public static final String CLAIM_USERNAME = "username";

    /**
     * JWT租户ID声明
     */
    public static final String CLAIM_TENANT_ID = "tenantId";

    /**
     * JWT类型声明
     */
    public static final String CLAIM_TYPE = "type";

    /**
     * JWT类型：访问令牌
     */
    public static final String TOKEN_TYPE_ACCESS = "access";

    /**
     * JWT类型：刷新令牌
     */
    public static final String TOKEN_TYPE_REFRESH = "refresh";

    /**
     * 默认访问令牌过期时间（秒）- 2小时
     */
    public static final long DEFAULT_ACCESS_EXPIRE = 7200;

    /**
     * 默认刷新令牌过期时间（秒）- 7天
     */
    public static final long DEFAULT_REFRESH_EXPIRE = 604800;

    private TokenConstant() {
    }
}

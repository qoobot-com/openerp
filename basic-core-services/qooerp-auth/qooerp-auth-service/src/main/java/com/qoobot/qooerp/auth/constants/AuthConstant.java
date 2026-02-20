package com.qoobot.qooerp.auth.constants;

/**
 * 认证常量
 */
public class AuthConstant {

    /**
     * 令牌黑名单Key前缀
     */
    public static final String TOKEN_BLACKLIST_KEY = "auth:token:blacklist:";

    /**
     * 刷新令牌Key前缀
     */
    public static final String REFRESH_TOKEN_KEY = "auth:refresh:";

    /**
     * 登录失败次数Key前缀
     */
    public static final String LOGIN_FAIL_COUNT_KEY = "auth:fail:count:";

    /**
     * IP登录失败Key前缀
     */
    public static final String IP_FAIL_COUNT_KEY = "auth:fail:ip:";

    /**
     * 用户信息缓存Key前缀
     */
    public static final String USER_INFO_KEY = "auth:user:info:";

    /**
     * 验证码Key前缀
     */
    public static final String CAPTCHA_KEY = "auth:captcha:";

    /**
     * 短信验证码Key前缀
     */
    public static final String SMS_CODE_KEY = "auth:sms:";

    /**
     * 邮箱验证码Key前缀
     */
    public static final String EMAIL_CODE_KEY = "auth:email:";

    /**
     * MFA临时密钥Key前缀
     */
    public static final String MFA_TEMP_KEY = "auth:mfa:temp:";

    /**
     * MFA短信验证码Key前缀
     */
    public static final String MFA_SMS_CODE_KEY = "auth:mfa:sms:";

    /**
     * MFA邮箱验证码Key前缀
     */
    public static final String MFA_EMAIL_CODE_KEY = "auth:mfa:email:";

    /**
     * 登录类型
     */
    public static final String LOGIN_TYPE_USERNAME = "username";
    public static final String LOGIN_TYPE_PHONE = "phone";
    public static final String LOGIN_TYPE_EMAIL = "email";

    /**
     * 用户状态
     */
    public static final Integer USER_STATUS_DISABLED = 0;
    public static final Integer USER_STATUS_ENABLED = 1;

    /**
     * MFA状态
     */
    public static final Integer MFA_DISABLED = 0;
    public static final Integer MFA_ENABLED = 1;

    /**
     * 默认头像
     */
    public static final String DEFAULT_AVATAR = "/avatar/default.png";

    private AuthConstant() {
    }
}

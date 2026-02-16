package com.qoobot.common.constant;

/**
 * 系统常量
 */
public class SystemConstants {

    /**
     * 默认密码
     */
    public static final String DEFAULT_PASSWORD = "123456";

    /**
     * 超级管理员账号
     */
    public static final String SUPER_ADMIN = "admin";

    /**
     * 超级管理员ID
     */
    public static final Long SUPER_ADMIN_ID = 1L;

    /**
     * 默认租户标识
     */
    public static final String DEFAULT_TENANT = "default";

    /**
     * 顶级部门ID
     */
    public static final Long TOP_DEPT_ID = 1L;

    /**
     * 系统管理员角色编码
     */
    public static final String SYSTEM_ADMIN_ROLE = "ROLE_ADMIN";

    /**
     * 默认头像
     */
    public static final String DEFAULT_AVATAR = "/static/images/default-avatar.png";

    /**
     * 用户名长度限制
     */
    public static final int USERNAME_MIN_LENGTH = 4;
    public static final int USERNAME_MAX_LENGTH = 20;

    /**
     * 密码长度限制
     */
    public static final int PASSWORD_MIN_LENGTH = 6;
    public static final int PASSWORD_MAX_LENGTH = 20;

    /**
     * 验证码长度
     */
    public static final int CAPTCHA_LENGTH = 4;

    /**
     * Token过期时间（秒）
     */
    public static final int TOKEN_EXPIRE_TIME = 60 * 60 * 24; // 24小时

    /**
     * 刷新Token过期时间（秒）
     */
    public static final int REFRESH_TOKEN_EXPIRE_TIME = 60 * 60 * 24 * 7; // 7天
}

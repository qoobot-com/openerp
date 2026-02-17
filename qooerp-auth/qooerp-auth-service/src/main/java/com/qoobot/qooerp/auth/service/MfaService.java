package com.qoobot.qooerp.auth.service;

import com.qoobot.qooerp.auth.dto.MfaSetupResponse;
import com.qoobot.qooerp.auth.entity.AuthUser;

/**
 * 多因素认证服务接口
 */
public interface MfaService {

    /**
     * 生成TOTP密钥（Google Authenticator）
     *
     * @param userId 用户ID
     * @param username 用户名
     * @return TOTP密钥和二维码URL
     */
    MfaSetupResponse generateTotpSecret(Long userId, String username);

    /**
     * 启用MFA（TOTP）
     *
     * @param userId 用户ID
     * @param secret 密钥
     * @param code 验证码
     * @return 是否成功
     */
    boolean enableTotpMfa(Long userId, String secret, String code);

    /**
     * 禁用MFA
     *
     * @param userId 用户ID
     * @param password 密码验证
     * @return 是否成功
     */
    boolean disableMfa(Long userId, String password);

    /**
     * 验证TOTP验证码
     *
     * @param userId 用户ID
     * @param code TOTP验证码
     * @return 是否验证成功
     */
    boolean verifyTotpCode(Long userId, String code);

    /**
     * 验证短信MFA验证码
     *
     * @param userId 用户ID
     * @param code 短信验证码
     * @return 是否验证成功
     */
    boolean verifySmsMfaCode(Long userId, String code);

    /**
     * 验证邮箱MFA验证码
     *
     * @param userId 用户ID
     * @param code 邮箱验证码
     * @return 是否验证成功
     */
    boolean verifyEmailMfaCode(Long userId, String code);

    /**
     * 检查用户是否启用MFA
     *
     * @param userId 用户ID
     * @return 是否启用
     */
    boolean isMfaEnabled(Long userId);
}

package com.qoobot.qooerp.auth.service;

import com.qoobot.qooerp.auth.entity.AuthUser;
import com.qoobot.qooerp.auth.dto.TokenResponse;

/**
 * 令牌服务接口
 */
public interface TokenService {

    /**
     * 生成访问令牌
     *
     * @param user 用户信息
     * @return 访问令牌
     */
    String generateAccessToken(AuthUser user);

    /**
     * 生成刷新令牌
     *
     * @param userId 用户ID
     * @return 刷新令牌
     */
    String generateRefreshToken(Long userId);

    /**
     * 生成令牌对
     *
     * @param user 用户信息
     * @return 令牌响应
     */
    TokenResponse generateTokenPair(AuthUser user);

    /**
     * 验证令牌
     *
     * @param token 令牌
     * @return 是否有效
     */
    boolean validateToken(String token);

    /**
     * 检查令牌是否在黑名单中
     *
     * @param token 令牌
     * @return 是否在黑名单中
     */
    boolean isTokenBlacklisted(String token);

    /**
     * 将令牌加入黑名单
     *
     * @param token 令牌
     */
    void addToBlacklist(String token);

    /**
     * 获取令牌过期时间
     *
     * @param token 令牌
     * @return 过期时间（秒）
     */
    Long getExpiresIn(String token);

    /**
     * 从令牌中获取用户ID
     *
     * @param token 令牌
     * @return 用户ID
     */
    Long getUserIdFromToken(String token);

    /**
     * 从令牌中获取用户名
     *
     * @param token 令牌
     * @return 用户名
     */
    String getUsernameFromToken(String token);

    /**
     * 检查令牌是否过期
     *
     * @param token 令牌
     * @return 是否过期
     */
    boolean isTokenExpired(String token);
}

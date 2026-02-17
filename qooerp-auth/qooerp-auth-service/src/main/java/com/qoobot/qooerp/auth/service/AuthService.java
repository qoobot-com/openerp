package com.qoobot.qooerp.auth.service;

import com.qoobot.qooerp.auth.dto.LoginRequest;
import com.qoobot.qooerp.auth.dto.LoginResponse;
import com.qoobot.qooerp.auth.dto.RefreshTokenRequest;
import com.qoobot.qooerp.auth.dto.TokenResponse;

/**
 * 认证服务接口
 */
public interface AuthService {

    /**
     * 用户登录
     */
    LoginResponse login(LoginRequest request);

    /**
     * 登出
     */
    void logout(String token);

    /**
     * 刷新令牌
     */
    TokenResponse refreshToken(RefreshTokenRequest request);

    /**
     * 验证令牌
     */
    boolean validateToken(String token);

    /**
     * 获取当前用户信息
     */
    LoginResponse getCurrentUser(String token);
}

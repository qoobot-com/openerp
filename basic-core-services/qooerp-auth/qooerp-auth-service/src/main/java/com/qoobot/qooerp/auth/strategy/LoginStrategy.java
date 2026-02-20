package com.qoobot.qooerp.auth.strategy;

import com.qoobot.qooerp.auth.dto.LoginRequest;
import com.qoobot.qooerp.auth.dto.LoginResponse;

/**
 * 登录策略接口
 */
public interface LoginStrategy {

    /**
     * 判断是否支持该登录类型
     *
     * @param loginType 登录类型
     * @return 是否支持
     */
    boolean supports(String loginType);

    /**
     * 执行登录
     *
     * @param request 登录请求
     * @return 登录响应
     */
    LoginResponse login(LoginRequest request);

    /**
     * 验证登录参数
     *
     * @param request 登录请求
     */
    void validate(LoginRequest request);
}

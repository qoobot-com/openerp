package com.qoobot.qooerp.auth.service;

import com.qoobot.qooerp.auth.dto.ThirdPartyLoginRequest;
import com.qoobot.qooerp.auth.dto.LoginResponse;

/**
 * 第三方登录服务接口
 */
public interface ThirdPartyLoginService {

    /**
     * 微信登录
     *
     * @param code 微信授权码
     * @return 登录响应
     */
    LoginResponse wechatLogin(String code);

    /**
     * 钉钉登录
     *
     * @param code 钉钉授权码
     * @return 登录响应
     */
    LoginResponse dingtalkLogin(String code);

    /**
     * 企业微信登录
     *
     * @param code 企业微信授权码
     * @return 登录响应
     */
    LoginResponse weComLogin(String code);

    /**
     * OAuth2.0登录（通用）
     *
     * @param request 第三方登录请求
     * @return 登录响应
     */
    LoginResponse oauth2Login(ThirdPartyLoginRequest request);

    /**
     * 绑定第三方账号
     *
     * @param userId 用户ID
     * @param platform 平台类型（wechat/dingtalk/wecom）
     * @param openid 第三方OpenID
     * @param unionid 第三方UnionID
     * @return 是否成功
     */
    boolean bindThirdParty(Long userId, String platform, String openid, String unionid);

    /**
     * 解绑第三方账号
     *
     * @param userId 用户ID
     * @param platform 平台类型
     * @return 是否成功
     */
    boolean unbindThirdParty(Long userId, String platform);
}

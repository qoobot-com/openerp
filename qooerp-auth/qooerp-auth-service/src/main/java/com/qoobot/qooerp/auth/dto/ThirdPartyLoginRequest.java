package com.qoobot.qooerp.auth.dto;

import lombok.Data;

/**
 * 第三方登录请求
 */
@Data
public class ThirdPartyLoginRequest {

    /**
     * 平台类型：wechat-微信 dingtalk-钉钉 wecom-企业微信
     */
    private String platform;

    /**
     * 授权码
     */
    private String code;

    /**
     * 状态（用于CSRF防护）
     */
    private String state;

    /**
     * OpenID
     */
    private String openid;

    /**
     * UnionID
     */
    private String unionid;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像
     */
    private String avatar;
}

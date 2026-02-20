package com.qoobot.qooerp.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 登录请求
 */
@Data
public class LoginRequest {

    /**
     * 登录类型：username-用户名密码，phone-手机验证码，email-邮箱验证码
     */
    private String loginType;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 手机验证码
     */
    private String phoneCode;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 邮箱验证码
     */
    private String emailCode;

    /**
     * 图形验证码
     */
    private String captchaCode;

    /**
     * 图形验证码key
     */
    private String captchaKey;

    /**
     * MFA验证码
     */
    private String mfaCode;

    /**
     * 登录IP
     */
    private String ip;

    /**
     * 登录地点
     */
    private String location;

    /**
     * 浏览器类型
     */
    private String browser;

    /**
     * 操作系统
     */
    private String os;


    /**
     * 验证码类型：0-字符验证码 1-滑块验证码
     */
    private Integer captchaType;


    /**
     * 滑块X坐标
     */
    private Integer captchaX;

    /**
     * 滑块Y坐标
     */
    private Integer captchaY;
}

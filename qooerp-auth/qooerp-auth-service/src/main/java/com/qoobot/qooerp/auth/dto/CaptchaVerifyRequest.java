package com.qoobot.qooerp.auth.dto;

import lombok.Data;

/**
 * 验证码验证请求
 */
@Data
public class CaptchaVerifyRequest {

    /**
     * 验证码Key
     */
    private String key;

    /**
     * 验证码类型：0-字符验证码 1-滑块验证码
     */
    private Integer type;

    /**
     * 验证码（字符验证码）
     */
    private String code;

    /**
     * 滑块X坐标（滑块验证码）
     */
    private Integer x;

    /**
     * 滑块Y坐标（滑块验证码）
     */
    private Integer y;
}

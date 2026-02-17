package com.qoobot.qooerp.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 验证码响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CaptchaResponse {

    /**
     * 验证码Key
     */
    private String key;

    /**
     * 验证码类型：0-字符验证码 1-滑块验证码
     */
    private Integer type;

    /**
     * 验证码图片（Base64）
     */
    private String image;

    /**
     * 背景图（滑块验证码）
     */
    private String backgroundImage;

    /**
     * 滑块图（滑块验证码）
     */
    private String sliderImage;

    /**
     * 滑块Y坐标
     */
    private Integer y;

    /**
     * 滑块X坐标（用于验证）
     */
    private Integer x;
}

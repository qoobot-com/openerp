package com.qoobot.qooerp.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 短信验证码请求
 */
@Data
public class SmsCodeRequest {

    /**
     * 手机号
     */
    @NotBlank(message = "手机号不能为空")
    private String phone;
}

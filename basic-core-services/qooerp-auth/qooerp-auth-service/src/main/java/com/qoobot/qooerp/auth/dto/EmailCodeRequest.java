package com.qoobot.qooerp.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 邮箱验证码请求
 */
@Data
public class EmailCodeRequest {

    /**
     * 邮箱
     */
    @NotBlank(message = "邮箱不能为空")
    private String email;
}

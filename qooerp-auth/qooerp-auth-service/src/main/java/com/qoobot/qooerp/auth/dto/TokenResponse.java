package com.qoobot.qooerp.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 令牌响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenResponse {

    /**
     * 访问令牌
     */
    private String accessToken;

    /**
     * 刷新令牌
     */
    private String refreshToken;

    /**
     * 令牌类型
     */
    @Builder.Default
    private String tokenType = "Bearer";

    /**
     * 过期时间（秒）
     */
    private Long expiresIn;
}

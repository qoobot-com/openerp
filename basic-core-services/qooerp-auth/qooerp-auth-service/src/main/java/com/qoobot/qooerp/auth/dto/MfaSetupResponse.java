package com.qoobot.qooerp.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * MFA设置响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MfaSetupResponse {

    /**
     * 密钥
     */
    private String secret;

    /**
     * 二维码URL
     */
    private String qrCodeUrl;

    /**
     * 备用码列表
     */
    private java.util.List<String> backupCodes;
}

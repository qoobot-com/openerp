package com.qoobot.qooerp.permission.config;

import com.qoobot.qooerp.permission.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 权限配置
 */
@Configuration
@RequiredArgsConstructor
public class PermissionConfig {

    private final PermissionService permissionService;

    /**
     * 权限验证器Bean
     */
    @Bean
    public PermissionService permissionService() {
        return permissionService;
    }
}

package com.qoobot.qooerp.hr.config;

import feign.RequestInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


/**
 * Feign配置
 *
 * @author QooERP Team
 * @since 2026-02-17
 */
@Configuration
public class FeignConfig {

    /**
     * Feign请求拦截器 - 传递认证信息
     */
    @Bean
    public RequestInterceptor requestInterceptor() {
        return template -> {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                // 传递Token
                String token = request.getHeader("Authorization");
                if (token != null) {
                    template.header("Authorization", token);
                }
                // 传递TraceId
                String traceId = request.getHeader("X-Trace-Id");
                if (traceId != null) {
                    template.header("X-Trace-Id", traceId);
                }
                // 传递租户ID
                String tenantId = request.getHeader("X-Tenant-Id");
                if (tenantId != null) {
                    template.header("X-Tenant-Id", tenantId);
                }
            }
        };
    }
}

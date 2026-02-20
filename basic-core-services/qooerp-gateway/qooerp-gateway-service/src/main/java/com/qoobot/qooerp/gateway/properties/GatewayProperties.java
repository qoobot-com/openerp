package com.qoobot.qooerp.gateway.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 网关自定义配置属性
 *
 * @author QooERP Team
 * @since 2026-02-17
 */
@Data
@Component("qooerpGatewayProperties")
@ConfigurationProperties(prefix = "qooerp.gateway")
public class GatewayProperties {

    /**
     * 是否启用认证过滤器
     */
    private boolean authEnabled = true;

    /**
     * 是否启用权限过滤器
     */
    private boolean permissionEnabled = true;

    /**
     * 是否启用日志过滤器
     */
    private boolean logEnabled = true;

    /**
     * 是否启用限流过滤器
     */
    private boolean rateLimitEnabled = true;

    /**
     * 默认限流速率（每秒请求数）
     */
    private long defaultReplenishRate = 100;

    /**
     * 默认突发容量
     */
    private long defaultBurstCapacity = 200;

    /**
     * Token请求头名称
     */
    private String tokenHeader = "Authorization";

    /**
     * Token前缀
     */
    private String tokenPrefix = "Bearer ";
}

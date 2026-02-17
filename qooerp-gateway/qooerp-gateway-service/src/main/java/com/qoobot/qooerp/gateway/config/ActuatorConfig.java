package com.qoobot.qooerp.gateway.config;

import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.context.annotation.Configuration;

/**
 * Actuator配置
 *
 * @author QooERP Team
 * @since 2026-02-17
 */
@Configuration
public class ActuatorConfig {

    /**
     * 自定义网关端点
     */
    @Endpoint(id = "gateway-info")
    @ConditionalOnAvailableEndpoint
    public static class GatewayInfoEndpoint {

        @ReadOperation
        public String gatewayInfo() {
            return "QooERP Gateway is running";
        }
    }
}

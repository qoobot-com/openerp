package com.qoobot.qooerp.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * 日志过滤器 - 记录请求和响应日志
 *
 * @author QooERP Team
 * @since 2026-02-17
 */
@Slf4j
@Component
public class LogFilter implements GlobalFilter, Ordered {

    private static final String REQUEST_START_TIME = "REQUEST_START_TIME";
    private static final String TRACE_ID = "TRACE_ID";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();
        String method = request.getMethod().name();
        String traceId = generateTraceId();

        // 记录请求开始时间
        exchange.getAttributes().put(REQUEST_START_TIME, System.currentTimeMillis());
        
        // 设置TraceId到MDC
        MDC.put(TRACE_ID, traceId);

        // 记录请求日志
        log.info("Gateway Request: {} {} from {}, TraceId: {}",
            method, path, getClientIp(request), traceId);

        // 将TraceId添加到请求头
        ServerHttpRequest modifiedRequest = request.mutate()
            .header("X-Trace-Id", traceId)
            .build();

        return chain.filter(exchange.mutate().request(modifiedRequest).build())
            .doFinally(signalType -> {
                // 计算请求耗时
                Long startTime = exchange.getAttribute(REQUEST_START_TIME);
                long duration = System.currentTimeMillis() - (startTime != null ? startTime : 0);
                
                // 记录响应日志
                int statusCode = exchange.getResponse().getStatusCode() != null ? 
                    exchange.getResponse().getStatusCode().value() : 0;
                log.info("Gateway Response: {} {} - {} ({}ms), TraceId: {}",
                    method, path, statusCode, duration, traceId);
                
                // 清除MDC
                MDC.remove(TRACE_ID);
            });
    }

    @Override
    public int getOrder() {
        return -80;
    }

    /**
     * 生成TraceId
     */
    private String generateTraceId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 获取客户端IP
     */
    private String getClientIp(ServerHttpRequest request) {
        String ip = request.getHeaders().getFirst("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeaders().getFirst("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddress() != null ? 
                request.getRemoteAddress().getAddress().getHostAddress() : "";
        }
        // 处理多个IP的情况
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}

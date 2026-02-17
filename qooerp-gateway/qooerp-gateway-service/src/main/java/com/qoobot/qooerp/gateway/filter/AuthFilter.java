package com.qoobot.qooerp.gateway.filter;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 全局认证过滤器
 *
 * @author QooERP Team
 * @since 2026-02-17
 */
@Slf4j
@Component
public class AuthFilter implements GlobalFilter, Ordered {

    private static final String TOKEN_HEADER = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";
    private static final String WHITE_LIST_PATH = "/api/auth/login";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();

        // 白名单路径直接放行
        if (isWhiteListPath(path)) {
            log.debug("白名单路径，放行: {}", path);
            return chain.filter(exchange);
        }

        // 获取Token
        String token = getToken(request);
        if (token == null || token.isEmpty()) {
            log.warn("请求未携带Token: {}", path);
            return unauthorized(exchange, "未登录或登录已过期");
        }

        // TODO: 验证Token有效性（调用认证服务或解析JWT）
        boolean isValid = validateToken(token);
        if (!isValid) {
            log.warn("Token无效或已过期: {}", path);
            return unauthorized(exchange, "Token无效或已过期");
        }

        // 将用户信息添加到请求头
        ServerHttpRequest modifiedRequest = request.mutate()
            .header("X-User-Id", extractUserId(token))
            .header("X-User-Name", extractUserName(token))
            .header("X-Tenant-Id", extractTenantId(token))
            .build();

        return chain.filter(exchange.mutate().request(modifiedRequest).build());
    }

    @Override
    public int getOrder() {
        return -100;
    }

    /**
     * 判断是否为白名单路径
     */
    private boolean isWhiteListPath(String path) {
        return path.startsWith(WHITE_LIST_PATH) ||
               path.contains("/health") ||
               path.contains("/actuator");
    }

    /**
     * 获取Token
     */
    private String getToken(ServerHttpRequest request) {
        String authorization = request.getHeaders().getFirst(TOKEN_HEADER);
        if (authorization != null && authorization.startsWith(TOKEN_PREFIX)) {
            return authorization.substring(TOKEN_PREFIX.length());
        }
        return null;
    }

    /**
     * 验证Token
     */
    private boolean validateToken(String token) {
        // TODO: 实际调用认证服务或解析JWT验证Token
        return true;
    }

    /**
     * 提取用户ID
     */
    private String extractUserId(String token) {
        // TODO: 从Token中解析用户ID
        return "1";
    }

    /**
     * 提取用户名
     */
    private String extractUserName(String token) {
        // TODO: 从Token中解析用户名
        return "admin";
    }

    /**
     * 提取租户ID
     */
    private String extractTenantId(String token) {
        // TODO: 从Token中解析租户ID
        return "1";
    }

    /**
     * 返回未授权响应
     */
    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> result = new HashMap<>();
        result.put("code", 401);
        result.put("message", message);
        result.put("timestamp", System.currentTimeMillis());

        DataBuffer buffer = response.bufferFactory()
            .wrap(JSON.toJSONString(result).getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }
}

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
import java.util.Map;

/**
 * 权限校验过滤器
 *
 * @author QooERP Team
 * @since 20xx-xx-xx
 */
@Slf4j
@Component
public class PermissionFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();
        String method = request.getMethod().name();

        // 白名单路径直接放行
        if (isWhiteListPath(path)) {
            log.debug("白名单路径，放行: {} {}", method, path);
            return chain.filter(exchange);
        }

        // 获取用户ID
        String userId = request.getHeaders().getFirst("X-User-Id");
        if (userId == null) {
            log.warn("请求未携带用户信息: {} {}", method, path);
            return forbidden(exchange, "用户信息缺失");
        }

        // TODO: 权限校验（调用权限服务或本地缓存）
        boolean hasPermission = checkPermission(userId, path, method);
        if (!hasPermission) {
            log.warn("用户[{}]无权限访问: {} {}", userId, method, path);
            return forbidden(exchange, "无权访问该资源");
        }

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -90;
    }

    /**
     * 判断是否为白名单路径
     */
    private boolean isWhiteListPath(String path) {
        return path.contains("/api/auth/login") ||
               path.contains("/health") ||
               path.contains("/actuator");
    }

    /**
     * 权限校验
     */
    private boolean checkPermission(String userId, String path, String method) {
        // TODO: 实际调用权限服务或从缓存中获取用户权限
        return true;
    }

    /**
     * 返回禁止访问响应
     */
    private Mono<Void> forbidden(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.FORBIDDEN);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> result = new HashMap<>();
        result.put("code", 403);
        result.put("message", message);
        result.put("timestamp", System.currentTimeMillis());

        DataBuffer buffer = response.bufferFactory()
            .wrap(JSON.toJSONString(result).getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }
}

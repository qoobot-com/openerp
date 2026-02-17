package com.qoobot.qooerp.gateway.filter;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 限流过滤器 - 基于Redis的分布式限流
 *
 * @author QooERP Team
 * @since 2026-02-17
 */
@Slf4j
@Component
public class RateLimitFilter implements GlobalFilter, Ordered {

    private final ReactiveRedisTemplate<String, String> reactiveRedisTemplate;

    // 默认限流配置：每秒100个请求，突发200个
    private static final long DEFAULT_REPLENISH_RATE = 100;
    private static final long DEFAULT_BURST_CAPACITY = 200;

    public RateLimitFilter(ReactiveRedisTemplate<String, String> reactiveRedisTemplate) {
        this.reactiveRedisTemplate = reactiveRedisTemplate;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();
        String method = request.getMethod().name();

        // 白名单路径不限流
        if (isWhiteListPath(path)) {
            return chain.filter(exchange);
        }

        // 获取客户端IP
        String clientIp = getClientIp(request);
        // 构建限流Key
        String key = buildRateLimitKey(clientIp, path, method);

        // 检查限流
        return tryAcquire(key, DEFAULT_REPLENISH_RATE, DEFAULT_BURST_CAPACITY)
            .flatMap(allowed -> {
                if (allowed) {
                    return chain.filter(exchange);
                } else {
                    log.warn("请求被限流: {} {} from IP: {}", method, path, clientIp);
                    return tooManyRequests(exchange, "访问过于频繁，请稍后再试");
                }
            });
    }

    @Override
    public int getOrder() {
        return -70;
    }

    /**
     * 判断是否为白名单路径
     */
    private boolean isWhiteListPath(String path) {
        return path.contains("/health") || path.contains("/actuator");
    }

    /**
     * 构建限流Key
     */
    private String buildRateLimitKey(String clientIp, String path, String method) {
        // 格式: rate_limit:ip:method:path
        String pathKey = path.replaceAll("/", "_");
        return "rate_limit:" + clientIp + ":" + method + ":" + pathKey;
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
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    /**
     * 尝试获取令牌（令牌桶算法）
     */
    private Mono<Boolean> tryAcquire(String key, long replenishRate, long burstCapacity) {
        // Lua脚本实现令牌桶算法
        String script = "local key = KEYS[1]\n" +
            "local rate = tonumber(ARGV[1])\n" +
            "local capacity = tonumber(ARGV[2])\n" +
            "local now = tonumber(ARGV[3])\n" +
            "local requested = tonumber(ARGV[4])\n" +
            "local ttl = tonumber(ARGV[5])\n" +
            "\n" +
            "local current = redis.call('hmget', key, 'tokens', 'last_refill_time')\n" +
            "local tokens = tonumber(current[1])\n" +
            "local last_refill_time = tonumber(current[2])\n" +
            "\n" +
            "if tokens == nil then\n" +
            "    tokens = capacity\n" +
            "    last_refill_time = now\n" +
            "end\n" +
            "\n" +
            "local delta = math.max(0, now - last_refill_time)\n" +
            "local filled_tokens = math.min(capacity, tokens + (delta * rate / 1000))\n" +
            "\n" +
            "if filled_tokens < requested then\n" +
            "    redis.call('hmset', key, 'tokens', filled_tokens, 'last_refill_time', now)\n" +
            "    redis.call('expire', key, ttl)\n" +
            "    return 0\n" +
            "else\n" +
            "    local new_tokens = filled_tokens - requested\n" +
            "    redis.call('hmset', key, 'tokens', new_tokens, 'last_refill_time', now)\n" +
            "    redis.call('expire', key, ttl)\n" +
            "    return 1\n" +
            "end";

        RedisScript<Long> redisScript = RedisScript.of(script, Long.class);

        List<String> keys = Collections.singletonList(key);
        List<String> args = Arrays.asList(
            String.valueOf(replenishRate),
            String.valueOf(burstCapacity),
            String.valueOf(System.currentTimeMillis()),
            String.valueOf(1),
            String.valueOf(60)
        );

        return reactiveRedisTemplate.execute(
            redisScript,
            keys,
            args
        ).next().map(result -> result != null && result.equals(1L));
    }

    /**
     * 返回请求过多响应
     */
    private Mono<Void> tooManyRequests(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> result = new HashMap<>();
        result.put("code", 429);
        result.put("message", message);
        result.put("timestamp", System.currentTimeMillis());

        DataBuffer buffer = response.bufferFactory()
            .wrap(JSON.toJSONString(result).getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }
}

package com.qoobot.qooerp.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * 链路追踪过滤器 - 传递TraceId和SpanId
 *
 * @author QooERP Team
 * @since 2026-02-17
 */
@Slf4j
@Component
public class TraceFilter implements GlobalFilter, Ordered {

    private static final String TRACE_ID_HEADER = "X-Trace-Id";
    private static final String SPAN_ID_HEADER = "X-Span-Id";
    private static final String PARENT_SPAN_ID_HEADER = "X-Parent-Span-Id";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        // 从请求头获取TraceId，如果不存在则生成新的
        String traceId = request.getHeaders().getFirst(TRACE_ID_HEADER);
        if (traceId == null || traceId.isEmpty()) {
            traceId = generateTraceId();
        }

        // 生成新的SpanId
        String spanId = generateSpanId();

        // 获取父SpanId
        String parentSpanId = request.getHeaders().getFirst(SPAN_ID_HEADER);

        // 将TraceId和SpanId添加到请求头
        ServerHttpRequest modifiedRequest = request.mutate()
            .header(TRACE_ID_HEADER, traceId)
            .header(SPAN_ID_HEADER, spanId)
            .header(PARENT_SPAN_ID_HEADER, parentSpanId != null ? parentSpanId : "")
            .build();

        log.debug("TraceId: {}, SpanId: {}, ParentSpanId: {}",
            traceId, spanId, parentSpanId);

        return chain.filter(exchange.mutate().request(modifiedRequest).build());
    }

    @Override
    public int getOrder() {
        return -90;
    }

    /**
     * 生成TraceId
     */
    private String generateTraceId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 生成SpanId
     */
    private String generateSpanId() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }
}

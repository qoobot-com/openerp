package com.qoobot.qooerp.monitor.dto;

import lombok.Data;

import java.time.Instant;
import java.util.Map;

/**
 * OTLP Trace 数据 DTO
 * 解析后的链路追踪数据
 */
@Data
public class OtlpTraceData {

    /**
     * 链路 ID
     */
    private String traceId;

    /**
     * Span ID
     */
    private String spanId;

    /**
     * 父 Span ID
     */
    private String parentSpanId;

    /**
     * Span 名称
     */
    private String spanName;

    /**
     * Span 类型（SERVER, CLIENT, PRODUCER, CONSUMER 等）
     */
    private String spanKind;

    /**
     * 时间戳
     */
    private Instant timestamp;

    /**
     * 持续时间（毫秒）
     */
    private Long durationMs;

    /**
     * 状态码
     */
    private String statusCode;

    /**
     * 状态消息
     */
    private String statusMessage;

    /**
     * 服务名称
     */
    private String serviceName;

    /**
     * 实例标识
     */
    private String instance;

    /**
     * 标签（tags/attributes）
     */
    private Map<String, String> tags;

    /**
     * 链路库
     */
    private Map<String, String> resource;
}

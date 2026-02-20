package com.qoobot.qooerp.monitor.dto;

import lombok.Data;

import java.time.Instant;
import java.util.Map;

/**
 * OTLP Log 数据 DTO
 * 解析后的日志数据
 */
@Data
public class OtlpLogData {

    /**
     * 时间戳
     */
    private Instant timestamp;

    /**
     * 日志级别
     */
    private String level;

    /**
     * 日志消息
     */
    private String message;

    /**
     * 链路 ID
     */
    private String traceId;

    /**
     * Span ID
     */
    private String spanId;

    /**
     * 日志器名称
     */
    private String loggerName;

    /**
     * 线程名称
     */
    private String threadName;

    /**
     * 服务名称
     */
    private String serviceName;

    /**
     * 实例标识
     */
    private String instance;

    /**
     * 上下文信息（context/attributes）
     */
    private Map<String, Object> context;

    /**
     * 标签（tags/attributes）
     */
    private Map<String, String> tags;

    /**
     * 异常信息（可选）
     */
    private OtlpLogException exception;
}

/**
 * 日志异常信息
 */
@Data
class OtlpLogException {

    /**
     * 异常类型
     */
    private String type;

    /**
     * 异常消息
     */
    private String message;

    /**
     * 异常堆栈
     */
    private String stacktrace;
}

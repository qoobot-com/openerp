package com.qoobot.qooerp.monitor.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.sql.Timestamp;

/**
 * 监控链路追踪实体
 * 支持 OTLP Traces 数据存储（TimescaleDB 超表）
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("monitor_trace")
public class MonitorTrace {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 时间戳（TimescaleDB 超表分区字段）
     */
    @TableField("time")
    private Timestamp time;

    /**
     * 链路 ID
     */
    @TableField("trace_id")
    private String traceId;

    /**
     * Span ID
     */
    @TableField("span_id")
    private String spanId;

    /**
     * 父 Span ID
     */
    @TableField("parent_span_id")
    private String parentSpanId;

    /**
     * Span 名称
     */
    @TableField("span_name")
    private String spanName;

    /**
     * Span 类型（SERVER, CLIENT, PRODUCER, CONSUMER 等）
     */
    @TableField("span_kind")
    private String spanKind;

    /**
     * 开始时间
     */
    @TableField("start_time")
    private Timestamp startTime;

    /**
     * 持续时间（毫秒）
     */
    @TableField("duration_ms")
    private Long durationMs;

    /**
     * 状态码
     */
    @TableField("status_code")
    private String statusCode;

    /**
     * 状态消息
     */
    @TableField("status_message")
    private String statusMessage;

    /**
     * 服务名称
     */
    @TableField("service_name")
    private String serviceName;

    /**
     * 实例标识
     */
    @TableField("instance")
    private String instance;

    /**
     * 标签（JSON 格式）
     */
    @TableField("tags")
    private String tags;

    /**
     * 日志（JSON 格式）
     */
    @TableField("logs")
    private String logs;

    /**
     * 租户 ID
     */
    @TableField("tenant_id")
    private Long tenantId;

    /**
     * 创建时间
     */
    @TableField(value = "created_time", fill = FieldFill.INSERT)
    private Timestamp createdTime;

    /**
     * 更新时间
     */
    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    private Timestamp updatedTime;

    /**
     * 是否删除（逻辑删除）
     */
    @TableField("deleted")
    @TableLogic
    private Integer deleted;
}

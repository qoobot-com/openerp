package com.qoobot.qooerp.monitor.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.sql.Timestamp;

/**
 * 监控日志实体
 * 支持 OTLP 日志数据存储
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("monitor_log")
public class MonitorLog {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 时间戳（TimescaleDB 超表分区字段）
     */
    @TableField("time")
    private Timestamp time;

    /**
     * 日志级别（TRACE, DEBUG, INFO, WARN, ERROR）
     */
    @TableField("level")
    private String level;

    /**
     * 日志器名称
     */
    @TableField("logger_name")
    private String loggerName;

    /**
     * 日志消息
     */
    @TableField("message")
    private String message;

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
     * 线程名称
     */
    @TableField("thread_name")
    private String threadName;

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
     * 上下文信息（JSON 格式）
     */
    @TableField("context")
    private String context;

    /**
     * IP 地址
     */
    @TableField("ip_address")
    private String ipAddress;

    /**
     * 用户 ID
     */
    @TableField("user_id")
    private Long userId;

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

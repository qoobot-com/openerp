package com.qoobot.qooerp.monitor.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * 监控指标实体
 * 支持 OTLP Metrics 数据存储（TimescaleDB 超表）
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("monitor_metrics")
public class MonitorMetrics {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 时间戳（TimescaleDB 超表分区字段）
     */
    @TableField("time")
    private Timestamp time;

    /**
     * 指标名称
     */
    @TableField("metric_name")
    private String metricName;

    /**
     * 指标类型（GAUGE, COUNTER, SUMMARY, HISTOGRAM）
     */
    @TableField("metric_type")
    private String metricType;

    /**
     * 指标值
     */
    @TableField("metric_value")
    private Double metricValue;

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
     * 指标单位
     */
    @TableField("metric_unit")
    private String metricUnit;

    /**
     * 描述
     */
    @TableField("description")
    private String description;

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

    /**
     * 兼容旧字段：别名到 timestamp
     */
    public Timestamp getTimestamp() {
        return time;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.time = timestamp;
    }
}

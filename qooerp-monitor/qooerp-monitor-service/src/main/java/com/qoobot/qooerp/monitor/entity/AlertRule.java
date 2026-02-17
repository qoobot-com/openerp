package com.qoobot.qooerp.monitor.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.sql.Timestamp;

/**
 * 告警规则实体
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("alert_rule")
public class AlertRule {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 规则名称
     */
    @TableField("rule_name")
    private String ruleName;

    /**
     * 规则描述
     */
    @TableField("description")
    private String description;

    /**
     * 规则类型（METRIC, TRACE, LOG）
     */
    @TableField("rule_type")
    private String ruleType;

    /**
     * 监控指标名称
     */
    @TableField("metric_name")
    private String metricName;

    /**
     * 服务名称（可选，空表示所有服务）
     */
    @TableField("service_name")
    private String serviceName;

    /**
     * 条件类型（GT, LT, GTE, LTE, EQ, NEQ）
     */
    @TableField("condition_type")
    private String conditionType;

    /**
     * 阈值
     */
    @TableField("threshold")
    private Double threshold;

    /**
     * 持续时间（秒），达到阈值持续多久才触发告警
     */
    @TableField("duration")
    private Long duration;

    /**
     * 检测间隔（秒）
     */
    @TableField("check_interval")
    private Long checkInterval;

    /**
     * 告警级别（INFO, WARNING, CRITICAL）
     */
    @TableField("severity")
    private String severity;

    /**
     * 告警标题
     */
    @TableField("alert_title")
    private String alertTitle;

    /**
     * 告警消息模板
     */
    @TableField("alert_message")
    private String alertMessage;

    /**
     * 通知渠道（JSON 数组：EMAIL, SMS, WEBHOOK, DINGTALK）
     */
    @TableField("notify_channels")
    private String notifyChannels;

    /**
     * 通知配置（JSON 格式）
     */
    @TableField("notify_config")
    private String notifyConfig;

    /**
     * 是否启用
     */
    @TableField("enabled")
    private Boolean enabled;

    /**
     * 静默开始时间
     */
    @TableField("silence_start")
    private Timestamp silenceStart;

    /**
     * 静默结束时间
     */
    @TableField("silence_end")
    private Timestamp silenceEnd;

    /**
     * 标签过滤（JSON 格式，如 {"env":"prod"}）
     */
    @TableField("label_filters")
    private String labelFilters;

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
     * 创建人
     */
    @TableField("created_by")
    private String createdBy;

    /**
     * 更新人
     */
    @TableField("updated_by")
    private String updatedBy;

    /**
     * 是否删除（逻辑删除）
     */
    @TableField("deleted")
    @TableLogic
    private Integer deleted;
}

package com.qoobot.qooerp.monitor.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.sql.Timestamp;

/**
 * 告警实例实体
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("alert_instance")
public class AlertInstance {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 告警规则 ID
     */
    @TableField("rule_id")
    private Long ruleId;

    /**
     * 告警规则名称
     */
    @TableField("rule_name")
    private String ruleName;

    /**
     * 告警级别（INFO, WARNING, CRITICAL）
     */
    @TableField("severity")
    private String severity;

    /**
     * 告警状态（PENDING, FIRING, RESOLVED）
     */
    @TableField("status")
    private String status;

    /**
     * 触发时间
     */
    @TableField("firing_time")
    private Timestamp firingTime;

    /**
     * 解决时间
     */
    @TableField("resolved_time")
    private Timestamp resolvedTime;

    /**
     * 持续时间（秒）
     */
    @TableField("duration")
    private Long duration;

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
     * 指标名称
     */
    @TableField("metric_name")
    private String metricName;

    /**
     * 当前值
     */
    @TableField("current_value")
    private Double currentValue;

    /**
     * 阈值
     */
    @TableField("threshold")
    private Double threshold;

    /**
     * 告警标题
     */
    @TableField("alert_title")
    private String alertTitle;

    /**
     * 告警消息
     */
    @TableField("alert_message")
    private String alertMessage;

    /**
     * 标签（JSON 格式）
     */
    @TableField("labels")
    private String labels;

    /**
     * 通知次数
     */
    @TableField("notify_count")
    private Integer notifyCount;

    /**
     * 最后通知时间
     */
    @TableField("last_notify_time")
    private Timestamp lastNotifyTime;

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

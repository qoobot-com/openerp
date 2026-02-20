package com.qoobot.qooerp.monitor.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("monitor_alert_rule")
public class MonitorAlertRule {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @TableField("rule_name")
    private String ruleName;

    @TableField("rule_type")
    private String ruleType;

    @TableField("metric_name")
    private String metricName;

    @TableField("condition_operator")
    private String conditionOperator;

    @TableField("threshold_value")
    private java.math.BigDecimal thresholdValue;

    @TableField("alert_level")
    private Integer alertLevel;

    @TableField("enabled")
    private Boolean enabled;

    @TableField("notification_channels")
    private String notificationChannels;

    @TableField("description")
    private String description;

    @TableField("tenant_id")
    private Long tenantId;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;

    @TableField("deleted")
    @TableLogic
    private Boolean deleted;
}

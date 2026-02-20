package com.qoobot.qooerp.monitor.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.sql.Timestamp;

/**
 * 告警历史记录实体
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("alert_history")
public class AlertHistory {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 告警实例 ID
     */
    @TableField("instance_id")
    private Long instanceId;

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
     * 状态变更（FIRING, RESOLVED, ACKNOWLEDGED）
     */
    @TableField("status_change")
    private String statusChange;

    /**
     * 变更时间
     */
    @TableField("change_time")
    private Timestamp changeTime;

    /**
     * 变更原因
     */
    @TableField("change_reason")
    private String changeReason;

    /**
     * 操作人
     */
    @TableField("operator")
    private String operator;

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
     * 是否删除（逻辑删除）
     */
    @TableField("deleted")
    @TableLogic
    private Integer deleted;
}

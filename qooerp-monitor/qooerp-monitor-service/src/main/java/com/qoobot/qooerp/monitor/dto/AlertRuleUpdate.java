package com.qoobot.qooerp.monitor.dto;

import lombok.Data;

import java.sql.Timestamp;

/**
 * 告警规则更新 DTO
 */
@Data
public class AlertRuleUpdate {

    /**
     * 规则名称
     */
    private String ruleName;

    /**
     * 规则描述
     */
    private String description;

    /**
     * 监控指标名称
     */
    private String metricName;

    /**
     * 服务名称（可选）
     */
    private String serviceName;

    /**
     * 条件类型（GT, LT, GTE, LTE, EQ, NEQ）
     */
    private String conditionType;

    /**
     * 阈值
     */
    private Double threshold;

    /**
     * 持续时间（秒）
     */
    private Long duration;

    /**
     * 检测间隔（秒）
     */
    private Long checkInterval;

    /**
     * 告警级别（INFO, WARNING, CRITICAL）
     */
    private String severity;

    /**
     * 告警标题
     */
    private String alertTitle;

    /**
     * 告警消息模板
     */
    private String alertMessage;

    /**
     * 通知渠道数组（EMAIL, SMS, WEBHOOK, DINGTALK）
     */
    private String[] notifyChannels;

    /**
     * 通知配置（JSON）
     */
    private String notifyConfig;

    /**
     * 是否启用
     */
    private Boolean enabled;

    /**
     * 静默开始时间
     */
    private Timestamp silenceStart;

    /**
     * 静默结束时间
     */
    private Timestamp silenceEnd;

    /**
     * 标签过滤（JSON）
     */
    private String labelFilters;
}

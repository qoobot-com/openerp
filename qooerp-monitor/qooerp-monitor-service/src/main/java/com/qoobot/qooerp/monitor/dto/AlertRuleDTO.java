package com.qoobot.qooerp.monitor.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AlertRuleDTO {
    private Long id;
    private String ruleName;
    private String ruleType;
    private String metricName;
    private String conditionOperator;
    private BigDecimal thresholdValue;
    private Integer alertLevel;
    private Boolean enabled;
    private String notificationChannels;
    private String description;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

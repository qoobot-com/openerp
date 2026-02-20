package com.qoobot.qooerp.monitor.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AlertRuleUpdateDTO {
    private String ruleName;
    private String ruleType;
    private String metricName;
    private String conditionOperator;
    private BigDecimal thresholdValue;
    private Integer alertLevel;
    private Boolean enabled;
    private String notificationChannels;
    private String description;
}

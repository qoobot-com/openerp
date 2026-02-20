package com.qoobot.qooerp.monitor.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Data
public class AlertRuleCreateDTO {
    @NotBlank(message = "规则名称不能为空")
    private String ruleName;

    @NotBlank(message = "规则类型不能为空")
    private String ruleType;

    private String metricName;

    @NotBlank(message = "条件操作符不能为空")
    private String conditionOperator;

    @NotNull(message = "阈值不能为空")
    private BigDecimal thresholdValue;

    @NotNull(message = "告警级别不能为空")
    private Integer alertLevel;

    private Boolean enabled;
    private String notificationChannels;
    private String description;
}

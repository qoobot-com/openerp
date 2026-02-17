package com.qoobot.qooerp.monitor.dto;

import lombok.Data;

/**
 * 告警规则查询 DTO
 */
@Data
public class AlertRuleQuery {

    /**
     * 规则名称（模糊查询）
     */
    private String ruleName;

    /**
     * 规则类型（METRIC, TRACE, LOG）
     */
    private String ruleType;

    /**
     * 告警级别（INFO, WARNING, CRITICAL）
     */
    private String severity;

    /**
     * 是否启用
     */
    private Boolean enabled;

    /**
     * 页码
     */
    private Integer page = 1;

    /**
     * 每页大小
     */
    private Integer size = 20;
}

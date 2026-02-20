package com.qoobot.qooerp.monitor.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AlertRuleQueryDTO extends Page<AlertRuleQueryDTO> {
    private String ruleName;
    private String ruleType;
    private Integer alertLevel;
    private Boolean enabled;
}

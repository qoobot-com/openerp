package com.qoobot.qooerp.monitor.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

@Data
public class AlertRuleQueryDTO extends Page<AlertRuleQueryDTO> {
    private String ruleName;
    private String ruleType;
    private Integer alertLevel;
    private Boolean enabled;
}

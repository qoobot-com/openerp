package com.qoobot.qooerp.monitor.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.monitor.dto.*;

public interface MonitorAlertRuleService {
    Long createRule(AlertRuleCreateDTO dto);

    void updateRule(Long id, AlertRuleUpdateDTO dto);

    void deleteRule(Long id);

    Page<AlertRuleDTO> listRules(AlertRuleQueryDTO dto);

    AlertRuleDTO getRule(Long id);

    void enableRule(Long id);

    void disableRule(Long id);
}

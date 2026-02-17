package com.qoobot.qooerp.hr.attendance.rule.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qoobot.qooerp.hr.attendance.rule.domain.HrAttendanceRule;
import com.qoobot.qooerp.hr.attendance.rule.mapper.HrAttendanceRuleMapper;
import com.qoobot.qooerp.hr.attendance.rule.service.IHrAttendanceRuleService;
import org.springframework.stereotype.Service;

/**
 * 考勤规则服务实现
 */
@Service
public class HrAttendanceRuleServiceImpl extends ServiceImpl<HrAttendanceRuleMapper, HrAttendanceRule> 
    implements IHrAttendanceRuleService {

    @Override
    public HrAttendanceRule createRule(HrAttendanceRule rule) {
        rule.setEnabled(true);
        rule.setCreateTime(java.time.LocalDateTime.now());
        save(rule);
        return rule;
    }

    @Override
    public void updateRule(HrAttendanceRule rule) {
        rule.setUpdateTime(java.time.LocalDateTime.now());
        updateById(rule);
    }

    @Override
    public void deleteRule(Long id) {
        removeById(id);
    }

    @Override
    public void enableRule(Long id) {
        HrAttendanceRule rule = getById(id);
        if (rule != null) {
            rule.setEnabled(true);
            rule.setUpdateTime(java.time.LocalDateTime.now());
            updateById(rule);
        }
    }

    @Override
    public void disableRule(Long id) {
        HrAttendanceRule rule = getById(id);
        if (rule != null) {
            rule.setEnabled(false);
            rule.setUpdateTime(java.time.LocalDateTime.now());
            updateById(rule);
        }
    }

    @Override
    public HrAttendanceRule getByDepartmentId(Long departmentId) {
        return lambdaQuery()
            .eq(HrAttendanceRule::getDepartmentId, departmentId)
            .eq(HrAttendanceRule::getEnabled, true)
            .one();
    }

    @Override
    public java.util.List<HrAttendanceRule> getEnabledRules() {
        return lambdaQuery()
            .eq(HrAttendanceRule::getEnabled, true)
            .list();
    }
}

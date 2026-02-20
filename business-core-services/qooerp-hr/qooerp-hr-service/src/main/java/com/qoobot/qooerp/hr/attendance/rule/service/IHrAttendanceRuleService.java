package com.qoobot.qooerp.hr.attendance.rule.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qoobot.qooerp.hr.attendance.rule.domain.HrAttendanceRule;
import java.util.List;

/**
 * 考勤规则服务接口
 */
public interface IHrAttendanceRuleService extends IService<HrAttendanceRule> {

    /**
     * 创建考勤规则
     */
    HrAttendanceRule createRule(HrAttendanceRule rule);

    /**
     * 更新考勤规则
     */
    void updateRule(HrAttendanceRule rule);

    /**
     * 删除考勤规则
     */
    void deleteRule(Long id);

    /**
     * 启用规则
     */
    void enableRule(Long id);

    /**
     * 禁用规则
     */
    void disableRule(Long id);

    /**
     * 按部门ID查询规则
     */
    HrAttendanceRule getByDepartmentId(Long departmentId);

    /**
     * 查询启用的规则
     */
    List<HrAttendanceRule> getEnabledRules();
}

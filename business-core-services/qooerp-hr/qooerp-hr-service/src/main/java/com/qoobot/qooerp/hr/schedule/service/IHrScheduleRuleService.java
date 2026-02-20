package com.qoobot.qooerp.hr.schedule.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qoobot.qooerp.hr.schedule.domain.HrScheduleRule;

/**
 * 排班规则服务接口
 *
 * @author QooERP Team
 * @since 20xx-xx-xx
 */
public interface IHrScheduleRuleService extends IService<HrScheduleRule> {

    /**
     * 创建排班规则
     *
     * @param rule 规则信息
     * @return 规则ID
     */
    Long createRule(HrScheduleRule rule);

    /**
     * 更新排班规则
     *
     * @param rule 规则信息
     * @return 是否成功
     */
    Boolean updateRule(HrScheduleRule rule);

    /**
     * 删除排班规则
     *
     * @param ruleId 规则ID
     * @return 是否成功
     */
    Boolean deleteRule(Long ruleId);

    /**
     * 根据部门获取排班规则
     *
     * @param deptId 部门ID
     * @return 规则列表
     */
    java.util.List<HrScheduleRule> getRulesByDept(Long deptId);
}

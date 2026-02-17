package com.qoobot.qooerp.hr.schedule.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qoobot.qooerp.hr.schedule.domain.HrSchedulePlan;

import java.time.LocalDate;
import java.util.List;

/**
 * 排班计划服务接口
 *
 * @author QooERP Team
 * @since 2026-02-17
 */
public interface IHrSchedulePlanService extends IService<HrSchedulePlan> {

    /**
     * 创建排班计划
     *
     * @param plan 计划信息
     * @return 计划ID
     */
    Long createPlan(HrSchedulePlan plan);

    /**
     * 批量创建排班计划
     *
     * @param plans 计划列表
     * @return 是否成功
     */
    Boolean batchCreatePlan(List<HrSchedulePlan> plans);

    /**
     * 更新排班计划
     *
     * @param plan 计划信息
     * @return 是否成功
     */
    Boolean updatePlan(HrSchedulePlan plan);

    /**
     * 删除排班计划
     *
     * @param planId 计划ID
     * @return 是否成功
     */
    Boolean deletePlan(Long planId);

    /**
     * 获取员工排班计划
     *
     * @param employeeId 员工ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 排班计划列表
     */
    List<HrSchedulePlan> getEmployeePlans(Long employeeId, LocalDate startDate, LocalDate endDate);

    /**
     * 根据日期获取排班计划
     *
     * @param employeeId 员工ID
     * @param date 日期
     * @return 排班计划
     */
    HrSchedulePlan getPlanByDate(Long employeeId, LocalDate date);

    /**
     * 根据排班规则自动生成排班计划
     *
     * @param ruleId 规则ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 是否成功
     */
    Boolean generatePlanByRule(Long ruleId, LocalDate startDate, LocalDate endDate);
}

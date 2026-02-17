package com.qoobot.qooerp.hr.performance.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qoobot.qooerp.hr.performance.domain.HrPerformance;
import com.qoobot.qooerp.common.result.Result;

import java.util.List;

/**
 * 绩效评估服务接口
 */
public interface IHrPerformanceService extends IService<HrPerformance> {

    /**
     * 创建绩效评估
     */
    Result<HrPerformance> createPerformance(HrPerformance performance);

    /**
     * 更新绩效评估
     */
    Result<Boolean> updatePerformance(HrPerformance performance);

    /**
     * 提交自评
     */
    Result<Boolean> submitSelfAssess(Long performanceId, String selfComment, java.math.BigDecimal selfScore);

    /**
     * 提交主管评估
     */
    Result<Boolean> submitSupervisorAssess(Long performanceId, java.math.BigDecimal totalScore, 
                                           String supervisorComment, String improvementPlan);

    /**
     * 删除绩效评估
     */
    Result<Boolean> deletePerformance(Long id);

    /**
     * 获取绩效评估详情
     */
    Result<HrPerformance> getPerformance(Long id);

    /**
     * 根据员工和周期获取绩效评估
     */
    Result<HrPerformance> getPerformanceByEmployeeAndCycle(Long employeeId, Long cycleId);

    /**
     * 分页查询员工绩效列表
     */
    Result<IPage<HrPerformance>> listEmployeePerformances(IPage<?> page, Long employeeId);

    /**
     * 分页查询周期绩效列表
     */
    Result<IPage<HrPerformance>> listCyclePerformances(IPage<?> page, Long cycleId);

    /**
     * 计算综合得分
     */
    java.math.BigDecimal calculateTotalScore(HrPerformance performance);

    /**
     * 确定绩效等级
     */
    String determinePerformanceLevel(java.math.BigDecimal totalScore);

    /**
     * 批量创建部门绩效评估
     */
    Result<List<HrPerformance>> batchCreatePerformance(Long departmentId, Long cycleId);
}

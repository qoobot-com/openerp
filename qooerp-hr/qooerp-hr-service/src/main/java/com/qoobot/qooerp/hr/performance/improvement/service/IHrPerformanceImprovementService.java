package com.qoobot.qooerp.hr.performance.improvement.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qoobot.qooerp.hr.performance.improvement.domain.HrPerformanceImprovement;
import java.util.List;

/**
 * 绩效改进服务接口
 */
public interface IHrPerformanceImprovementService extends IService<HrPerformanceImprovement> {

    /**
     * 创建改进计划
     */
    HrPerformanceImprovement createImprovement(HrPerformanceImprovement improvement);

    /**
     * 更新改进计划
     */
    void updateImprovement(HrPerformanceImprovement improvement);

    /**
     * 删除改进计划
     */
    void deleteImprovement(Long id);

    /**
     * 开始改进计划
     */
    void startImprovement(Long id);

    /**
     * 更新完成进度
     */
    void updateProgress(Long id, Double percent, String status);

    /**
     * 完成改进计划
     */
    void completeImprovement(Long id, String effect, Double score);

    /**
     * 审核改进计划
     */
    void reviewImprovement(Long id, String reviewer, String comment);

    /**
     * 取消改进计划
     */
    void cancelImprovement(Long id);

    /**
     * 按绩效评估ID查询改进计划
     */
    List<HrPerformanceImprovement> getByPerformanceId(Long performanceId);

    /**
     * 按员工ID查询改进计划
     */
    List<HrPerformanceImprovement> getByEmployeeId(Long employeeId);

    /**
     * 查询进行中的改进计划
     */
    List<HrPerformanceImprovement> getInProgressImprovements();

    /**
     * 查询待审核的改进计划
     */
    List<HrPerformanceImprovement> getPendingReviewImprovements();
}

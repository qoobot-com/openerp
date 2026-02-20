package com.qoobot.qooerp.hr.performance.improvement.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qoobot.qooerp.hr.performance.improvement.domain.HrPerformanceImprovement;
import com.qoobot.qooerp.hr.performance.improvement.mapper.HrPerformanceImprovementMapper;
import com.qoobot.qooerp.hr.performance.improvement.service.IHrPerformanceImprovementService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

/**
 * 绩效改进服务实现
 */
@Service
public class HrPerformanceImprovementServiceImpl extends ServiceImpl<HrPerformanceImprovementMapper, HrPerformanceImprovement> 
    implements IHrPerformanceImprovementService {

    @Override
    public HrPerformanceImprovement createImprovement(HrPerformanceImprovement improvement) {
        improvement.setStatus("DRAFT");
        improvement.setCompletionPercent(0.0);
        improvement.setCreateTime(java.time.LocalDateTime.now());
        save(improvement);
        return improvement;
    }

    @Override
    public void updateImprovement(HrPerformanceImprovement improvement) {
        improvement.setUpdateTime(java.time.LocalDateTime.now());
        updateById(improvement);
    }

    @Override
    public void deleteImprovement(Long id) {
        removeById(id);
    }

    @Override
    public void startImprovement(Long id) {
        HrPerformanceImprovement improvement = getById(id);
        if (improvement != null) {
            improvement.setStatus("IN_PROGRESS");
            improvement.setUpdateTime(java.time.LocalDateTime.now());
            updateById(improvement);
        }
    }

    @Override
    public void updateProgress(Long id, Double percent, String status) {
        HrPerformanceImprovement improvement = getById(id);
        if (improvement != null) {
            improvement.setCompletionPercent(percent);
            improvement.setCompletionStatus(status);
            improvement.setUpdateTime(java.time.LocalDateTime.now());
            updateById(improvement);
        }
    }

    @Override
    public void completeImprovement(Long id, String effect, Double score) {
        HrPerformanceImprovement improvement = getById(id);
        if (improvement != null) {
            improvement.setStatus("REVIEWING");
            improvement.setCompletionPercent(100.0);
            improvement.setImprovementEffect(effect);
            improvement.setImprovementScore(score);
            improvement.setUpdateTime(java.time.LocalDateTime.now());
            updateById(improvement);
        }
    }

    @Override
    public void reviewImprovement(Long id, String reviewer, String comment) {
        HrPerformanceImprovement improvement = getById(id);
        if (improvement != null) {
            improvement.setStatus("COMPLETED");
            improvement.setReviewer(reviewer);
            improvement.setReviewDate(LocalDate.now());
            improvement.setReviewComment(comment);
            improvement.setUpdateTime(java.time.LocalDateTime.now());
            updateById(improvement);
        }
    }

    @Override
    public void cancelImprovement(Long id) {
        HrPerformanceImprovement improvement = getById(id);
        if (improvement != null) {
            improvement.setStatus("CANCELLED");
            improvement.setUpdateTime(java.time.LocalDateTime.now());
            updateById(improvement);
        }
    }

    @Override
    public java.util.List<HrPerformanceImprovement> getByPerformanceId(Long performanceId) {
        return lambdaQuery()
            .eq(HrPerformanceImprovement::getPerformanceId, performanceId)
            .orderByDesc(HrPerformanceImprovement::getCreateTime)
            .list();
    }

    @Override
    public java.util.List<HrPerformanceImprovement> getByEmployeeId(Long employeeId) {
        return lambdaQuery()
            .eq(HrPerformanceImprovement::getEmployeeId, employeeId)
            .orderByDesc(HrPerformanceImprovement::getCreateTime)
            .list();
    }

    @Override
    public java.util.List<HrPerformanceImprovement> getInProgressImprovements() {
        return lambdaQuery()
            .eq(HrPerformanceImprovement::getStatus, "IN_PROGRESS")
            .list();
    }

    @Override
    public java.util.List<HrPerformanceImprovement> getPendingReviewImprovements() {
        return lambdaQuery()
            .eq(HrPerformanceImprovement::getStatus, "REVIEWING")
            .list();
    }
}

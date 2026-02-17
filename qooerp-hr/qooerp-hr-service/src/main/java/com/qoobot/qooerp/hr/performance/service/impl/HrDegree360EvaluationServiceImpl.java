package com.qoobot.qooerp.hr.performance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qoobot.qooerp.common.result.Result;
import com.qoobot.qooerp.hr.performance.domain.HrDegree360Evaluation;
import com.qoobot.qooerp.hr.performance.mapper.HrDegree360EvaluationMapper;
import com.qoobot.qooerp.hr.performance.service.IHrDegree360EvaluationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 360度评估服务实现
 */
@Service
public class HrDegree360EvaluationServiceImpl extends ServiceImpl<HrDegree360EvaluationMapper, HrDegree360Evaluation> 
        implements IHrDegree360EvaluationService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<HrDegree360Evaluation> submitEvaluation(HrDegree360Evaluation evaluation) {
        // 计算综合得分
        BigDecimal totalScore = BigDecimal.ZERO;
        int count = 0;

        if (evaluation.getAbilityScore() != null) {
            totalScore = totalScore.add(evaluation.getAbilityScore());
            count++;
        }
        if (evaluation.getAttitudeScore() != null) {
            totalScore = totalScore.add(evaluation.getAttitudeScore());
            count++;
        }
        if (evaluation.getTeamworkScore() != null) {
            totalScore = totalScore.add(evaluation.getTeamworkScore());
            count++;
        }
        if (evaluation.getCommunicationScore() != null) {
            totalScore = totalScore.add(evaluation.getCommunicationScore());
            count++;
        }
        if (evaluation.getInnovationScore() != null) {
            totalScore = totalScore.add(evaluation.getInnovationScore());
            count++;
        }
        if (evaluation.getExecutionScore() != null) {
            totalScore = totalScore.add(evaluation.getExecutionScore());
            count++;
        }

        if (count > 0) {
            evaluation.setTotalScore(totalScore.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP));
        }

        evaluation.setCreateTime(LocalDateTime.now());
        evaluation.setUpdateTime(LocalDateTime.now());
        boolean success = this.save(evaluation);
        return success ? Result.success(evaluation) : Result.error("提交失败");
    }

    @Override
    public Result<HrDegree360Evaluation> getEvaluation(Long id) {
        HrDegree360Evaluation evaluation = this.getById(id);
        return evaluation != null ? Result.success(evaluation) : Result.error("评估记录不存在");
    }

    @Override
    public Result<IPage<HrDegree360Evaluation>> listPerformanceEvaluations(IPage<?> page, Long performanceId) {
        LambdaQueryWrapper<HrDegree360Evaluation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HrDegree360Evaluation::getPerformanceId, performanceId);
        wrapper.orderByDesc(HrDegree360Evaluation::getCreateTime);

        IPage<HrDegree360Evaluation> result = this.page((Page<HrDegree360Evaluation>) page, wrapper);
        return Result.success(result);
    }

    @Override
    public Result<IPage<HrDegree360Evaluation>> listEmployeeEvaluations(IPage<?> page, Long employeeId) {
        LambdaQueryWrapper<HrDegree360Evaluation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HrDegree360Evaluation::getEmployeeId, employeeId);
        wrapper.orderByDesc(HrDegree360Evaluation::getCreateTime);

        IPage<HrDegree360Evaluation> result = this.page((Page<HrDegree360Evaluation>) page, wrapper);
        return Result.success(result);
    }

    @Override
    public BigDecimal calculateAverageScore(Long performanceId) {
        LambdaQueryWrapper<HrDegree360Evaluation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HrDegree360Evaluation::getPerformanceId, performanceId);

        List<HrDegree360Evaluation> evaluations = this.list(wrapper);
        if (evaluations == null || evaluations.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal sum = BigDecimal.ZERO;
        for (HrDegree360Evaluation eval : evaluations) {
            if (eval.getTotalScore() != null) {
                sum = sum.add(eval.getTotalScore());
            }
        }

        return sum.divide(BigDecimal.valueOf(evaluations.size()), 2, RoundingMode.HALF_UP);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> deleteEvaluation(Long id) {
        HrDegree360Evaluation evaluation = this.getById(id);
        if (evaluation == null) {
            return Result.error("评估记录不存在");
        }

        boolean success = this.removeById(id);
        return success ? Result.success(true) : Result.error("删除失败");
    }
}

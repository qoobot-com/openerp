package com.qoobot.qooerp.hr.performance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qoobot.qooerp.common.result.Result;
import com.qoobot.qooerp.hr.employee.domain.HrEmployee;
import com.qoobot.qooerp.hr.employee.mapper.HrEmployeeMapper;
import com.qoobot.qooerp.hr.performance.domain.HrPerformance;
import com.qoobot.qooerp.hr.performance.mapper.HrPerformanceMapper;
import com.qoobot.qooerp.hr.performance.service.IHrPerformanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 绩效评估服务实现
 */
@Service
@RequiredArgsConstructor
public class HrPerformanceServiceImpl extends ServiceImpl<HrPerformanceMapper, HrPerformance> 
        implements IHrPerformanceService {

    private final HrEmployeeMapper employeeMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<HrPerformance> createPerformance(HrPerformance performance) {
        // 查询员工信息
        HrEmployee employee = employeeMapper.selectById(performance.getEmployeeId());
        if (employee == null) {
            return Result.error("员工不存在");
        }

        performance.setEmployeeName(employee.getName());
        performance.setPerformanceNo("PERF" + System.currentTimeMillis() + (int)(Math.random() * 1000));
        performance.setStatus(0); // 待评估
        performance.setCreateTime(LocalDateTime.now());
        performance.setUpdateTime(LocalDateTime.now());

        boolean success = this.save(performance);
        return success ? Result.success(performance) : Result.error("创建失败");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> updatePerformance(HrPerformance performance) {
        HrPerformance existing = this.getById(performance.getId());
        if (existing == null) {
            return Result.error("绩效评估不存在");
        }

        performance.setUpdateTime(LocalDateTime.now());
        boolean success = this.updateById(performance);
        return success ? Result.success(true) : Result.error("更新失败");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> submitSelfAssess(Long performanceId, String selfComment, BigDecimal selfScore) {
        HrPerformance performance = this.getById(performanceId);
        if (performance == null) {
            return Result.error("绩效评估不存在");
        }
        if (performance.getStatus() != 0 && performance.getStatus() != 1) {
            return Result.error("当前状态不支持自评");
        }

        performance.setSelfComment(selfComment);
        performance.setSelfScore(selfScore);
        performance.setStatus(1); // 自评中 -> 主管评估中
        performance.setUpdateTime(LocalDateTime.now());

        boolean success = this.updateById(performance);
        return success ? Result.success(true) : Result.error("提交失败");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> submitSupervisorAssess(Long performanceId, BigDecimal totalScore, 
                                                     String supervisorComment, String improvementPlan) {
        HrPerformance performance = this.getById(performanceId);
        if (performance == null) {
            return Result.error("绩效评估不存在");
        }
        if (performance.getStatus() != 1) {
            return Result.error("请先完成自评");
        }

        performance.setTotalScore(totalScore);
        performance.setPerformanceLevel(determinePerformanceLevel(totalScore));
        performance.setSupervisorComment(supervisorComment);
        performance.setImprovementPlan(improvementPlan);
        performance.setStatus(3); // 已评估
        performance.setUpdateTime(LocalDateTime.now());

        boolean success = this.updateById(performance);
        return success ? Result.success(true) : Result.error("提交失败");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> deletePerformance(Long id) {
        HrPerformance performance = this.getById(id);
        if (performance == null) {
            return Result.error("绩效评估不存在");
        }
        if (performance.getStatus() == 3) {
            return Result.error("已完成的评估无法删除");
        }

        boolean success = this.removeById(id);
        return success ? Result.success(true) : Result.error("删除失败");
    }

    @Override
    public Result<HrPerformance> getPerformance(Long id) {
        HrPerformance performance = this.getById(id);
        return performance != null ? Result.success(performance) : Result.error("绩效评估不存在");
    }

    @Override
    public Result<HrPerformance> getPerformanceByEmployeeAndCycle(Long employeeId, Long cycleId) {
        LambdaQueryWrapper<HrPerformance> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HrPerformance::getEmployeeId, employeeId);
        wrapper.eq(HrPerformance::getCycleId, cycleId);
        HrPerformance performance = this.getOne(wrapper);
        return Result.success(performance);
    }

    @Override
    public Result<IPage<HrPerformance>> listEmployeePerformances(IPage<?> page, Long employeeId) {
        LambdaQueryWrapper<HrPerformance> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HrPerformance::getEmployeeId, employeeId);
        wrapper.orderByDesc(HrPerformance::getAssessYear, HrPerformance::getAssessMonth);

        IPage<HrPerformance> result = this.page((Page<HrPerformance>) page, wrapper);
        return Result.success(result);
    }

    @Override
    public Result<IPage<HrPerformance>> listCyclePerformances(IPage<?> page, Long cycleId) {
        LambdaQueryWrapper<HrPerformance> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HrPerformance::getCycleId, cycleId);
        wrapper.orderByDesc(HrPerformance::getTotalScore);

        IPage<HrPerformance> result = this.page((Page<HrPerformance>) page, wrapper);
        return Result.success(result);
    }

    @Override
    public BigDecimal calculateTotalScore(HrPerformance performance) {
        BigDecimal totalScore = BigDecimal.ZERO;

        // KPI得分
        if (performance.getKpiScore() != null && performance.getKpiWeight() != null) {
            totalScore = totalScore.add(performance.getKpiScore().multiply(performance.getKpiWeight().divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)));
        }

        // 工作态度得分
        if (performance.getAttitudeScore() != null && performance.getAttitudeWeight() != null) {
            totalScore = totalScore.add(performance.getAttitudeScore().multiply(performance.getAttitudeWeight().divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)));
        }

        // 能力素质得分
        if (performance.getAbilityScore() != null && performance.getAbilityWeight() != null) {
            totalScore = totalScore.add(performance.getAbilityScore().multiply(performance.getAbilityWeight().divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)));
        }

        // 360度评分
        if (performance.getDegree360Score() != null && performance.getDegree360Weight() != null) {
            totalScore = totalScore.add(performance.getDegree360Score().multiply(performance.getDegree360Weight().divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)));
        }

        return totalScore.setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public String determinePerformanceLevel(BigDecimal totalScore) {
        if (totalScore == null) {
            return "D";
        }

        if (totalScore.compareTo(BigDecimal.valueOf(90)) >= 0) {
            return "A"; // 优秀
        } else if (totalScore.compareTo(BigDecimal.valueOf(80)) >= 0) {
            return "B"; // 良好
        } else if (totalScore.compareTo(BigDecimal.valueOf(60)) >= 0) {
            return "C"; // 合格
        } else {
            return "D"; // 需改进
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<List<HrPerformance>> batchCreatePerformance(Long departmentId, Long cycleId) {
        // TODO: 实现批量创建部门绩效评估
        return Result.success(new ArrayList<>());
    }
}

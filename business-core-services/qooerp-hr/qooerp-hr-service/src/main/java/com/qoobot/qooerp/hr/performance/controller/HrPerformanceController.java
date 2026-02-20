package com.qoobot.qooerp.hr.performance.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.common.result.Result;
import com.qoobot.qooerp.hr.performance.domain.HrPerformance;
import com.qoobot.qooerp.hr.performance.service.IHrPerformanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * 绩效评估控制器
 */
@RestController
@RequestMapping("/api/hr/performance")
@RequiredArgsConstructor
public class HrPerformanceController {

    private final IHrPerformanceService performanceService;

    @PostMapping("/create")
    public Result<HrPerformance> create(@RequestBody HrPerformance performance) {
        return performanceService.createPerformance(performance);
    }

    @PutMapping("/update")
    public Result<Boolean> update(@RequestBody HrPerformance performance) {
        return performanceService.updatePerformance(performance);
    }

    @PostMapping("/self-assess")
    public Result<Boolean> submitSelfAssess(
            @RequestParam Long performanceId,
            @RequestParam String selfComment,
            @RequestParam BigDecimal selfScore) {
        return performanceService.submitSelfAssess(performanceId, selfComment, selfScore);
    }

    @PostMapping("/supervisor-assess")
    public Result<Boolean> submitSupervisorAssess(
            @RequestParam Long performanceId,
            @RequestParam BigDecimal totalScore,
            @RequestParam String supervisorComment,
            @RequestParam(required = false) String improvementPlan) {
        return performanceService.submitSupervisorAssess(performanceId, totalScore, supervisorComment, improvementPlan);
    }

    @DeleteMapping("/delete/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return performanceService.deletePerformance(id);
    }

    @GetMapping("/get/{id}")
    public Result<HrPerformance> get(@PathVariable Long id) {
        return performanceService.getPerformance(id);
    }

    @GetMapping("/by-employee-cycle")
    public Result<HrPerformance> getByEmployeeAndCycle(
            @RequestParam Long employeeId,
            @RequestParam Long cycleId) {
        return performanceService.getPerformanceByEmployeeAndCycle(employeeId, cycleId);
    }

    @GetMapping("/employee-list")
    public Result<IPage<HrPerformance>> listEmployee(
            @RequestParam Long employeeId,
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {
        return performanceService.listEmployeePerformances(new Page<>(current, size), employeeId);
    }

    @GetMapping("/cycle-list")
    public Result<IPage<HrPerformance>> listCycle(
            @RequestParam Long cycleId,
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {
        return performanceService.listCyclePerformances(new Page<>(current, size), cycleId);
    }

    @PostMapping("/batch-create")
    public Result<List<HrPerformance>> batchCreate(
            @RequestParam Long departmentId,
            @RequestParam Long cycleId) {
        return performanceService.batchCreatePerformance(departmentId, cycleId);
    }
}

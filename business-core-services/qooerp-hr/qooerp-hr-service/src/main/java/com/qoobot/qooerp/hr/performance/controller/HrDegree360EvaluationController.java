package com.qoobot.qooerp.hr.performance.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.common.result.Result;
import com.qoobot.qooerp.hr.performance.domain.HrDegree360Evaluation;
import com.qoobot.qooerp.hr.performance.service.IHrDegree360EvaluationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 360度评估控制器
 */
@RestController
@RequestMapping("/api/hr/degree360-evaluation")
@RequiredArgsConstructor
public class HrDegree360EvaluationController {

    private final IHrDegree360EvaluationService degree360EvaluationService;

    @PostMapping("/submit")
    public Result<HrDegree360Evaluation> submit(@RequestBody HrDegree360Evaluation evaluation) {
        return degree360EvaluationService.submitEvaluation(evaluation);
    }

    @GetMapping("/get/{id}")
    public Result<HrDegree360Evaluation> get(@PathVariable Long id) {
        return degree360EvaluationService.getEvaluation(id);
    }

    @GetMapping("/performance-list")
    public Result<IPage<HrDegree360Evaluation>> listPerformance(
            @RequestParam Long performanceId,
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {
        return degree360EvaluationService.listPerformanceEvaluations(new Page<>(current, size), performanceId);
    }

    @GetMapping("/employee-list")
    public Result<IPage<HrDegree360Evaluation>> listEmployee(
            @RequestParam Long employeeId,
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {
        return degree360EvaluationService.listEmployeeEvaluations(new Page<>(current, size), employeeId);
    }

    @DeleteMapping("/delete/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return degree360EvaluationService.deleteEvaluation(id);
    }
}

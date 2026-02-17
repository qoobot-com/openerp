package com.qoobot.qooerp.hr.performance.improvement.controller;

import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.hr.performance.improvement.domain.HrPerformanceImprovement;
import com.qoobot.qooerp.hr.performance.improvement.service.IHrPerformanceImprovementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 绩效改进控制器
 */
@RestController
@RequestMapping("/hr/performance/improvement")
public class HrPerformanceImprovementController {

    @Autowired
    private IHrPerformanceImprovementService improvementService;

    /**
     * 创建改进计划
     */
    @PostMapping("/create")
    public Result<HrPerformanceImprovement> create(@RequestBody HrPerformanceImprovement improvement) {
        return Result.success(improvementService.createImprovement(improvement));
    }

    /**
     * 更新改进计划
     */
    @PutMapping("/update")
    public Result<Void> update(@RequestBody HrPerformanceImprovement improvement) {
        improvementService.updateImprovement(improvement);
        return Result.success();
    }

    /**
     * 删除改进计划
     */
    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        improvementService.deleteImprovement(id);
        return Result.success();
    }

    /**
     * 开始改进计划
     */
    @PostMapping("/start/{id}")
    public Result<Void> start(@PathVariable Long id) {
        improvementService.startImprovement(id);
        return Result.success();
    }

    /**
     * 更新完成进度
     */
    @PostMapping("/updateProgress/{id}")
    public Result<Void> updateProgress(@PathVariable Long id, @RequestParam Double percent, @RequestParam String status) {
        improvementService.updateProgress(id, percent, status);
        return Result.success();
    }

    /**
     * 完成改进计划
     */
    @PostMapping("/complete/{id}")
    public Result<Void> complete(@PathVariable Long id, @RequestParam String effect, @RequestParam Double score) {
        improvementService.completeImprovement(id, effect, score);
        return Result.success();
    }

    /**
     * 审核改进计划
     */
    @PostMapping("/review/{id}")
    public Result<Void> review(@PathVariable Long id, @RequestParam String reviewer, @RequestParam String comment) {
        improvementService.reviewImprovement(id, reviewer, comment);
        return Result.success();
    }

    /**
     * 取消改进计划
     */
    @PostMapping("/cancel/{id}")
    public Result<Void> cancel(@PathVariable Long id) {
        improvementService.cancelImprovement(id);
        return Result.success();
    }

    /**
     * 查询改进计划详情
     */
    @GetMapping("/detail/{id}")
    public Result<HrPerformanceImprovement> detail(@PathVariable Long id) {
        return Result.success(improvementService.getById(id));
    }

    /**
     * 按绩效评估查询改进计划
     */
    @GetMapping("/byPerformance/{performanceId}")
    public Result<List<HrPerformanceImprovement>> getByPerformance(@PathVariable Long performanceId) {
        return Result.success(improvementService.getByPerformanceId(performanceId));
    }

    /**
     * 按员工查询改进计划
     */
    @GetMapping("/byEmployee/{employeeId}")
    public Result<List<HrPerformanceImprovement>> getByEmployee(@PathVariable Long employeeId) {
        return Result.success(improvementService.getByEmployeeId(employeeId));
    }

    /**
     * 查询进行中的改进计划
     */
    @GetMapping("/inProgressList")
    public Result<List<HrPerformanceImprovement>> inProgressList() {
        return Result.success(improvementService.getInProgressImprovements());
    }

    /**
     * 查询待审核的改进计划
     */
    @GetMapping("/pendingReviewList")
    public Result<List<HrPerformanceImprovement>> pendingReviewList() {
        return Result.success(improvementService.getPendingReviewImprovements());
    }
}

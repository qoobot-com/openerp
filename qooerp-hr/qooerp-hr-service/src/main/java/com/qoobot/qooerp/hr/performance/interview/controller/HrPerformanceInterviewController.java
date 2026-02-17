package com.qoobot.qooerp.hr.performance.interview.controller;

import com.qoobot.qooerp.common.core.domain.Result;
import com.qoobot.qooerp.hr.performance.interview.domain.HrPerformanceInterview;
import com.qoobot.qooerp.hr.performance.interview.service.IHrPerformanceInterviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 绩效面谈控制器
 */
@RestController
@RequestMapping("/hr/performance/interview")
public class HrPerformanceInterviewController {

    @Autowired
    private IHrPerformanceInterviewService interviewService;

    /**
     * 创建绩效面谈
     */
    @PostMapping("/create")
    public Result<HrPerformanceInterview> create(@RequestBody HrPerformanceInterview interview) {
        return Result.success(interviewService.createInterview(interview));
    }

    /**
     * 更新绩效面谈
     */
    @PutMapping("/update")
    public Result<Void> update(@RequestBody HrPerformanceInterview interview) {
        interviewService.updateInterview(interview);
        return Result.success();
    }

    /**
     * 删除绩效面谈
     */
    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        interviewService.deleteInterview(id);
        return Result.success();
    }

    /**
     * 完成面谈
     */
    @PostMapping("/complete/{id}")
    public Result<Void> complete(@PathVariable Long id) {
        interviewService.completeInterview(id);
        return Result.success();
    }

    /**
     * 取消面谈
     */
    @PostMapping("/cancel/{id}")
    public Result<Void> cancel(@PathVariable Long id) {
        interviewService.cancelInterview(id);
        return Result.success();
    }

    /**
     * 员工签字确认
     */
    @PostMapping("/sign/{id}")
    public Result<Void> sign(@PathVariable Long id) {
        interviewService.employeeSign(id);
        return Result.success();
    }

    /**
     * 查询面谈详情
     */
    @GetMapping("/detail/{id}")
    public Result<HrPerformanceInterview> detail(@PathVariable Long id) {
        return Result.success(interviewService.getById(id));
    }

    /**
     * 按绩效评估查询面谈
     */
    @GetMapping("/byPerformance/{performanceId}")
    public Result<List<HrPerformanceInterview>> getByPerformance(@PathVariable Long performanceId) {
        return Result.success(interviewService.getByPerformanceId(performanceId));
    }

    /**
     * 按员工查询面谈记录
     */
    @GetMapping("/byEmployee/{employeeId}")
    public Result<List<HrPerformanceInterview>> getByEmployee(@PathVariable Long employeeId) {
        return Result.success(interviewService.getByEmployeeId(employeeId));
    }

    /**
     * 查询待完成面谈列表
     */
    @GetMapping("/scheduledList")
    public Result<List<HrPerformanceInterview>> scheduledList() {
        return Result.success(interviewService.getScheduledInterviews());
    }
}

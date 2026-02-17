package com.qoobot.qooerp.hr.salary.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.common.result.Result;
import com.qoobot.qooerp.hr.salary.domain.HrSalaryAdjust;
import com.qoobot.qooerp.hr.salary.service.IHrSalaryAdjustService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 薪酬调整控制器
 */
@RestController
@RequestMapping("/api/hr/salary-adjust")
@RequiredArgsConstructor
public class HrSalaryAdjustController {

    private final IHrSalaryAdjustService salaryAdjustService;

    @PostMapping("/submit")
    public Result<HrSalaryAdjust> submit(@RequestBody HrSalaryAdjust adjust) {
        return salaryAdjustService.submitAdjust(adjust);
    }

    @PostMapping("/approve")
    public Result<Boolean> approve(
            @RequestParam Long adjustId,
            @RequestParam Boolean approved,
            @RequestParam(required = false) String remark) {
        return salaryAdjustService.approveAdjust(adjustId, approved, remark);
    }

    @PostMapping("/cancel/{adjustId}")
    public Result<Boolean> cancel(@PathVariable Long adjustId) {
        return salaryAdjustService.cancelAdjust(adjustId);
    }

    @GetMapping("/get/{id}")
    public Result<HrSalaryAdjust> get(@PathVariable Long id) {
        return salaryAdjustService.getAdjust(id);
    }

    @GetMapping("/employee-history")
    public Result<IPage<HrSalaryAdjust>> listEmployeeHistory(
            @RequestParam Long employeeId,
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {
        return salaryAdjustService.listEmployeeAdjusts(new Page<>(current, size), employeeId);
    }

    @GetMapping("/pending")
    public Result<IPage<HrSalaryAdjust>> listPending(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {
        return salaryAdjustService.listPendingApprovals(new Page<>(current, size));
    }
}

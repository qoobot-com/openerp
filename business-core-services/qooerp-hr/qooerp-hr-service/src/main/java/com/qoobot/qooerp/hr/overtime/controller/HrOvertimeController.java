package com.qoobot.qooerp.hr.overtime.controller;

import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.hr.overtime.domain.HrOvertime;
import com.qoobot.qooerp.hr.overtime.service.IHrOvertimeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 加班管理控制器
 *
 * @author QooERP Team
 * @since 20xx-xx-xx
 */
@Slf4j
@RestController
@RequestMapping("/overtime")
@RequiredArgsConstructor
@Tag(name = "加班管理", description = "加班申请与审批")
@Validated
public class HrOvertimeController {

    private final IHrOvertimeService overtimeService;

    @Operation(summary = "提交加班申请")
    @PostMapping
    public Result<Long> submitOvertime(@Valid @RequestBody HrOvertime overtime) {
        Long overtimeId = overtimeService.submitOvertime(overtime);
        return Result.success(overtimeId);
    }

    @Operation(summary = "审批加班申请")
    @PostMapping("/approve/{overtimeId}")
    public Result<Boolean> approveOvertime(
            @PathVariable Long overtimeId,
            @RequestParam Long approverId,
            @RequestParam Boolean approved,
            @RequestParam(required = false) String comment) {
        Boolean result = overtimeService.approveOvertime(overtimeId, approverId, approved, comment);
        return Result.success(result);
    }

    @Operation(summary = "撤销加班申请")
    @PostMapping("/cancel/{overtimeId}")
    public Result<Boolean> cancelOvertime(@PathVariable Long overtimeId) {
        Boolean result = overtimeService.cancelOvertime(overtimeId);
        return Result.success(result);
    }

    @Operation(summary = "获取加班申请详情")
    @GetMapping("/{overtimeId}")
    public Result<HrOvertime> getOvertime(@PathVariable Long overtimeId) {
        HrOvertime overtime = overtimeService.getById(overtimeId);
        return Result.success(overtime);
    }

    @Operation(summary = "获取我的加班申请")
    @GetMapping("/my/{employeeId}")
    public Result<List<HrOvertime>> getMyOvertime(@PathVariable Long employeeId) {
        List<HrOvertime> list = overtimeService.getMyOvertime(employeeId);
        return Result.success(list);
    }

    @Operation(summary = "获取待审批的加班申请")
    @GetMapping("/pending/{approverId}")
    public Result<List<HrOvertime>> getPendingApproval(@PathVariable Long approverId) {
        List<HrOvertime> list = overtimeService.getPendingApproval(approverId);
        return Result.success(list);
    }

    @Operation(summary = "使用加班调休")
    @PostMapping("/compensatory/{overtimeId}")
    public Result<Boolean> useCompensatoryLeave(@PathVariable Long overtimeId) {
        Boolean result = overtimeService.useCompensatoryLeave(overtimeId);
        return Result.success(result);
    }
}

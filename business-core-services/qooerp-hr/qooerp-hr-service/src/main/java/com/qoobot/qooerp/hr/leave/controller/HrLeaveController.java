package com.qoobot.qooerp.hr.leave.controller;

import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.hr.leave.domain.HrLeave;
import com.qoobot.qooerp.hr.leave.service.IHrLeaveService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 请假管理控制器
 *
 * @author QooERP Team
 * @since 20xx-xx-xx
 */
@Slf4j
@RestController
@RequestMapping("/leave")
@RequiredArgsConstructor
@Tag(name = "请假管理", description = "请假申请与审批")
@Validated
public class HrLeaveController {

    private final IHrLeaveService leaveService;

    @Operation(summary = "提交请假申请")
    @PostMapping
    public Result<Long> submitLeave(@Valid @RequestBody HrLeave leave) {
        Long leaveId = leaveService.submitLeave(leave);
        return Result.success(leaveId);
    }

    @Operation(summary = "审批请假申请")
    @PostMapping("/approve/{leaveId}")
    public Result<Boolean> approveLeave(
            @PathVariable Long leaveId,
            @RequestParam Long approverId,
            @RequestParam Boolean approved,
            @RequestParam(required = false) String comment) {
        Boolean result = leaveService.approveLeave(leaveId, approverId, approved, comment);
        return Result.success(result);
    }

    @Operation(summary = "撤销请假申请")
    @PostMapping("/cancel/{leaveId}")
    public Result<Boolean> cancelLeave(@PathVariable Long leaveId) {
        Boolean result = leaveService.cancelLeave(leaveId);
        return Result.success(result);
    }

    @Operation(summary = "获取请假申请详情")
    @GetMapping("/{leaveId}")
    public Result<HrLeave> getLeave(@PathVariable Long leaveId) {
        HrLeave leave = leaveService.getById(leaveId);
        return Result.success(leave);
    }

    @Operation(summary = "获取我的请假申请")
    @GetMapping("/my/{employeeId}")
    public Result<List<HrLeave>> getMyLeave(@PathVariable Long employeeId) {
        List<HrLeave> list = leaveService.getMyLeave(employeeId);
        return Result.success(list);
    }

    @Operation(summary = "获取待审批的请假申请")
    @GetMapping("/pending/{approverId}")
    public Result<List<HrLeave>> getPendingApproval(@PathVariable Long approverId) {
        List<HrLeave> list = leaveService.getPendingApproval(approverId);
        return Result.success(list);
    }
}

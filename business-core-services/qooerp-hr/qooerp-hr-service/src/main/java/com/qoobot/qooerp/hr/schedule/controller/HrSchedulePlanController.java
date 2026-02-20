package com.qoobot.qooerp.hr.schedule.controller;

import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.hr.schedule.domain.HrSchedulePlan;
import com.qoobot.qooerp.hr.schedule.service.IHrSchedulePlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 排班计划管理控制器
 *
 * @author QooERP Team
 * @since 20xx-xx-xx
 */
@Slf4j
@RestController
@RequestMapping("/schedule-plan")
@RequiredArgsConstructor
@Tag(name = "排班计划管理", description = "排班计划管理")
@Validated
public class HrSchedulePlanController {

    private final IHrSchedulePlanService planService;

    @Operation(summary = "创建排班计划")
    @PostMapping
    public Result<Long> createPlan(@Valid @RequestBody HrSchedulePlan plan) {
        Long planId = planService.createPlan(plan);
        return Result.success(planId);
    }

    @Operation(summary = "批量创建排班计划")
    @PostMapping("/batch")
    public Result<Boolean> batchCreatePlan(@Valid @RequestBody List<HrSchedulePlan> plans) {
        Boolean result = planService.batchCreatePlan(plans);
        return Result.success(result);
    }

    @Operation(summary = "更新排班计划")
    @PutMapping
    public Result<Boolean> updatePlan(@Valid @RequestBody HrSchedulePlan plan) {
        Boolean result = planService.updatePlan(plan);
        return Result.success(result);
    }

    @Operation(summary = "删除排班计划")
    @DeleteMapping("/{planId}")
    public Result<Boolean> deletePlan(@PathVariable Long planId) {
        Boolean result = planService.deletePlan(planId);
        return Result.success(result);
    }

    @Operation(summary = "获取排班计划详情")
    @GetMapping("/{planId}")
    public Result<HrSchedulePlan> getPlan(@PathVariable Long planId) {
        HrSchedulePlan plan = planService.getById(planId);
        return Result.success(plan);
    }

    @Operation(summary = "获取员工排班计划")
    @GetMapping("/employee/{employeeId}")
    public Result<List<HrSchedulePlan>> getEmployeePlans(
            @PathVariable Long employeeId,
            @RequestParam String startDate,
            @RequestParam String endDate) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        List<HrSchedulePlan> list = planService.getEmployeePlans(employeeId, start, end);
        return Result.success(list);
    }

    @Operation(summary = "根据日期获取排班计划")
    @GetMapping("/date/{employeeId}/{date}")
    public Result<HrSchedulePlan> getPlanByDate(
            @PathVariable Long employeeId,
            @PathVariable String date) {
        LocalDate planDate = LocalDate.parse(date);
        HrSchedulePlan plan = planService.getPlanByDate(employeeId, planDate);
        return Result.success(plan);
    }

    @Operation(summary = "根据规则自动生成排班计划")
    @PostMapping("/generate/{ruleId}")
    public Result<Boolean> generatePlanByRule(
            @PathVariable Long ruleId,
            @RequestParam String startDate,
            @RequestParam String endDate) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        Boolean result = planService.generatePlanByRule(ruleId, start, end);
        return Result.success(result);
    }
}

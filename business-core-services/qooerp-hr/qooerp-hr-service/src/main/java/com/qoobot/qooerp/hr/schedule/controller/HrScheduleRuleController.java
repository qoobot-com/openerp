package com.qoobot.qooerp.hr.schedule.controller;

import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.hr.schedule.domain.HrScheduleRule;
import com.qoobot.qooerp.hr.schedule.service.IHrScheduleRuleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 排班规则管理控制器
 *
 * @author QooERP Team
 * @since 20xx-xx-xx
 */
@Slf4j
@RestController
@RequestMapping("/schedule-rule")
@RequiredArgsConstructor
@Tag(name = "排班规则管理", description = "排班规则管理")
@Validated
public class HrScheduleRuleController {

    private final IHrScheduleRuleService ruleService;

    @Operation(summary = "创建排班规则")
    @PostMapping
    public Result<Long> createRule(@Valid @RequestBody HrScheduleRule rule) {
        Long ruleId = ruleService.createRule(rule);
        return Result.success(ruleId);
    }

    @Operation(summary = "更新排班规则")
    @PutMapping
    public Result<Boolean> updateRule(@Valid @RequestBody HrScheduleRule rule) {
        Boolean result = ruleService.updateRule(rule);
        return Result.success(result);
    }

    @Operation(summary = "删除排班规则")
    @DeleteMapping("/{ruleId}")
    public Result<Boolean> deleteRule(@PathVariable Long ruleId) {
        Boolean result = ruleService.deleteRule(ruleId);
        return Result.success(result);
    }

    @Operation(summary = "获取排班规则详情")
    @GetMapping("/{ruleId}")
    public Result<HrScheduleRule> getRule(@PathVariable Long ruleId) {
        HrScheduleRule rule = ruleService.getById(ruleId);
        return Result.success(rule);
    }

    @Operation(summary = "根据部门获取排班规则")
    @GetMapping("/dept/{deptId}")
    public Result<List<HrScheduleRule>> getRulesByDept(@PathVariable Long deptId) {
        List<HrScheduleRule> list = ruleService.getRulesByDept(deptId);
        return Result.success(list);
    }
}

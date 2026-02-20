package com.qoobot.qooerp.hr.attendance.rule.controller;

import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.hr.attendance.rule.domain.HrAttendanceRule;
import com.qoobot.qooerp.hr.attendance.rule.service.IHrAttendanceRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 考勤规则控制器
 */
@RestController
@RequestMapping("/hr/attendance/rule")
public class HrAttendanceRuleController {

    @Autowired
    private IHrAttendanceRuleService ruleService;

    /**
     * 创建考勤规则
     */
    @PostMapping("/create")
    public Result<HrAttendanceRule> create(@RequestBody HrAttendanceRule rule) {
        return Result.success(ruleService.createRule(rule));
    }

    /**
     * 更新考勤规则
     */
    @PutMapping("/update")
    public Result<Void> update(@RequestBody HrAttendanceRule rule) {
        ruleService.updateRule(rule);
        return Result.success();
    }

    /**
     * 删除考勤规则
     */
    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        ruleService.deleteRule(id);
        return Result.success();
    }

    /**
     * 启用规则
     */
    @PostMapping("/enable/{id}")
    public Result<Void> enable(@PathVariable Long id) {
        ruleService.enableRule(id);
        return Result.success();
    }

    /**
     * 禁用规则
     */
    @PostMapping("/disable/{id}")
    public Result<Void> disable(@PathVariable Long id) {
        ruleService.disableRule(id);
        return Result.success();
    }

    /**
     * 查询规则详情
     */
    @GetMapping("/detail/{id}")
    public Result<HrAttendanceRule> detail(@PathVariable Long id) {
        return Result.success(ruleService.getById(id));
    }

    /**
     * 按部门查询规则
     */
    @GetMapping("/byDepartment/{departmentId}")
    public Result<HrAttendanceRule> getByDepartment(@PathVariable Long departmentId) {
        return Result.success(ruleService.getByDepartmentId(departmentId));
    }

    /**
     * 查询所有规则
     */
    @GetMapping("/list")
    public Result<List<HrAttendanceRule>> list() {
        return Result.success(ruleService.list());
    }

    /**
     * 查询启用的规则
     */
    @GetMapping("/enabledList")
    public Result<List<HrAttendanceRule>> enabledList() {
        return Result.success(ruleService.getEnabledRules());
    }
}

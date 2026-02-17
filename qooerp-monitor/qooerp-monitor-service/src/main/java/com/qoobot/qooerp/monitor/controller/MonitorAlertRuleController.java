package com.qoobot.qooerp.monitor.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.monitor.dto.*;
import com.qoobot.qooerp.monitor.service.MonitorAlertRuleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/monitor/alert-rule")
@RequiredArgsConstructor
public class MonitorAlertRuleController {

    private final MonitorAlertRuleService ruleService;

    @PostMapping("/create")
    public Result<Long> createRule(@Valid @RequestBody AlertRuleCreateDTO dto) {
        Long id = ruleService.createRule(dto);
        return Result.success(id);
    }

    @PutMapping("/{id}")
    public Result<Void> updateRule(@PathVariable Long id, @Valid @RequestBody AlertRuleUpdateDTO dto) {
        ruleService.updateRule(id, dto);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteRule(@PathVariable Long id) {
        ruleService.deleteRule(id);
        return Result.success();
    }

    @PostMapping("/list")
    public Result<Page<AlertRuleDTO>> listRules(@RequestBody AlertRuleQueryDTO dto) {
        Page<AlertRuleDTO> page = ruleService.listRules(dto);
        return Result.success(page);
    }

    @GetMapping("/{id}")
    public Result<AlertRuleDTO> getRule(@PathVariable Long id) {
        AlertRuleDTO dto = ruleService.getRule(id);
        return Result.success(dto);
    }

    @PutMapping("/{id}/enable")
    public Result<Void> enableRule(@PathVariable Long id) {
        ruleService.enableRule(id);
        return Result.success();
    }

    @PutMapping("/{id}/disable")
    public Result<Void> disableRule(@PathVariable Long id) {
        ruleService.disableRule(id);
        return Result.success();
    }
}

package com.qoobot.qooerp.monitor.controller;

import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.monitor.dto.AlertRuleCreate;
import com.qoobot.qooerp.monitor.dto.AlertRuleQuery;
import com.qoobot.qooerp.monitor.dto.AlertRuleUpdate;
import com.qoobot.qooerp.monitor.dto.AlertInstanceQuery;
import com.qoobot.qooerp.monitor.entity.AlertRule;
import com.qoobot.qooerp.monitor.entity.AlertInstance;
import com.qoobot.qooerp.monitor.entity.AlertHistory;
import com.qoobot.qooerp.monitor.service.AlertEngineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

/**
 * 告警引擎 Controller
 */
@Tag(name = "告警引擎管理", description = "告警规则、实例管理")
@RestController
@RequestMapping("/monitor/api/alert")
@RequiredArgsConstructor
@Validated
public class AlertEngineController {

    private final AlertEngineService alertEngineService;

    // ========== 告警规则管理 ==========

    @Operation(summary = "创建告警规则")
    @PostMapping("/rule")
    public Result<AlertRule> createRule(@Validated @RequestBody AlertRuleCreate dto) {
        AlertRule rule = alertEngineService.createRule(dto);
        return Result.success(rule);
    }

    @Operation(summary = "更新告警规则")
    @PutMapping("/rule/{id}")
    public Result<AlertRule> updateRule(
        @Parameter(description = "规则 ID") @PathVariable Long id,
        @Validated @RequestBody AlertRuleUpdate dto
    ) {
        AlertRule rule = alertEngineService.updateRule(id, dto);
        return Result.success(rule);
    }

    @Operation(summary = "删除告警规则")
    @DeleteMapping("/rule/{id}")
    public Result<Void> deleteRule(@Parameter(description = "规则 ID") @PathVariable Long id) {
        alertEngineService.deleteRule(id);
        return Result.success();
    }

    @Operation(summary = "获取告警规则")
    @GetMapping("/rule/{id}")
    public Result<AlertRule> getRule(@Parameter(description = "规则 ID") @PathVariable Long id) {
        AlertRule rule = alertEngineService.getRule(id);
        return Result.success(rule);
    }

    @Operation(summary = "查询告警规则列表")
    @GetMapping("/rule/list")
    public Result<Map<String, Object>> listRules(AlertRuleQuery query) {
        Map<String, Object> result = alertEngineService.listRules(query);
        return Result.success(result);
    }

    @Operation(summary = "启用/禁用告警规则")
    @PutMapping("/rule/{id}/toggle")
    public Result<Void> toggleRule(
        @Parameter(description = "规则 ID") @PathVariable Long id,
        @Parameter(description = "是否启用") @RequestParam Boolean enabled
    ) {
        alertEngineService.toggleRule(id, enabled);
        return Result.success();
    }

    @Operation(summary = "手动检查告警规则")
    @PostMapping("/rule/{id}/check")
    public Result<Void> checkRule(@Parameter(description = "规则 ID") @PathVariable Long id) {
        alertEngineService.checkRule(id);
        return Result.success();
    }

    @Operation(summary = "批量检查所有告警规则")
    @PostMapping("/rule/check-all")
    public Result<Void> checkAllRules() {
        alertEngineService.checkAllRules();
        return Result.success();
    }

    @Operation(summary = "设置规则静默")
    @PutMapping("/rule/{id}/silence")
    public Result<Void> setSilence(
        @Parameter(description = "规则 ID") @PathVariable Long id,
        @Parameter(description = "静默开始时间") @RequestParam Timestamp silenceStart,
        @Parameter(description = "静默结束时间") @RequestParam Timestamp silenceEnd
    ) {
        alertEngineService.setSilence(id, silenceStart, silenceEnd);
        return Result.success();
    }

    @Operation(summary = "取消规则静默")
    @DeleteMapping("/rule/{id}/silence")
    public Result<Void> cancelSilence(@Parameter(description = "规则 ID") @PathVariable Long id) {
        alertEngineService.cancelSilence(id);
        return Result.success();
    }

    // ========== 告警实例管理 ==========

    @Operation(summary = "查询告警实例列表")
    @GetMapping("/instance/list")
    public Result<Map<String, Object>> listInstances(AlertInstanceQuery query) {
        Map<String, Object> result = alertEngineService.listInstances(query);
        return Result.success(result);
    }

    @Operation(summary = "获取告警实例")
    @GetMapping("/instance/{id}")
    public Result<AlertInstance> getInstance(@Parameter(description = "实例 ID") @PathVariable Long id) {
        AlertInstance instance = alertEngineService.getInstance(id);
        return Result.success(instance);
    }

    @Operation(summary = "确认告警")
    @PutMapping("/instance/{id}/acknowledge")
    public Result<Void> acknowledgeInstance(
        @Parameter(description = "实例 ID") @PathVariable Long id,
        @Parameter(description = "确认原因") @RequestParam(required = false) String reason
    ) {
        alertEngineService.acknowledgeInstance(id, reason);
        return Result.success();
    }

    @Operation(summary = "解决告警")
    @PutMapping("/instance/{id}/resolve")
    public Result<Void> resolveInstance(
        @Parameter(description = "实例 ID") @PathVariable Long id,
        @Parameter(description = "解决原因") @RequestParam(required = false) String reason
    ) {
        alertEngineService.resolveInstance(id, reason);
        return Result.success();
    }

    @Operation(summary = "重新通知告警")
    @PostMapping("/instance/{id}/renotify")
    public Result<Void> renotifyInstance(@Parameter(description = "实例 ID") @PathVariable Long id) {
        alertEngineService.renotifyInstance(id);
        return Result.success();
    }

    // ========== 告警历史 ==========

    @Operation(summary = "查询告警历史")
    @GetMapping("/instance/{id}/history")
    public Result<List<AlertHistory>> listHistory(@Parameter(description = "实例 ID") @PathVariable Long id) {
        List<AlertHistory> history = alertEngineService.listHistory(id);
        return Result.success(history);
    }

    // ========== 统计信息 ==========

    @Operation(summary = "获取告警统计")
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getStatistics() {
        Map<String, Object> stats = alertEngineService.getStatistics();
        return Result.success(stats);
    }
}

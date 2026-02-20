package com.qoobot.qooerp.workflow.controller;

import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.workflow.service.WorkflowTimeoutService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 流程超时处理控制器
 */
@Tag(name = "流程超时处理", description = "任务超时检测、提醒、自动处理等功能")
@RestController
@RequestMapping("/timeout")
@RequiredArgsConstructor
public class WorkflowTimeoutController {

    private final WorkflowTimeoutService workflowTimeoutService;

    @Operation(summary = "检查超时任务")
    @GetMapping("/check")
    public Result<List<Map<String, Object>>> checkTimeoutTasks() {
        List<Map<String, Object>> timeoutTasks = workflowTimeoutService.checkTimeoutTasks();
        return Result.success(timeoutTasks);
    }

    @Operation(summary = "处理超时任务")
    @PostMapping("/handle/{taskId}")
    public Result<Void> handleTimeoutTask(
            @PathVariable String taskId,
            @RequestBody Map<String, Object> request) {
        String action = (String) request.get("action");
        @SuppressWarnings("unchecked")
        Map<String, Object> actionParams = (Map<String, Object>) request.get("actionParams");

        workflowTimeoutService.handleTimeoutTask(taskId, action, actionParams);
        return Result.success();
    }

    @Operation(summary = "设置任务超时提醒")
    @PostMapping("/reminder/set/{taskId}")
    public Result<Void> setTimeoutReminder(
            @PathVariable String taskId,
            @RequestParam int remindBefore) {
        workflowTimeoutService.setTimeoutReminder(taskId, remindBefore);
        return Result.success();
    }

    @Operation(summary = "取消任务超时提醒")
    @DeleteMapping("/reminder/{taskId}")
    public Result<Void> cancelTimeoutReminder(@PathVariable String taskId) {
        workflowTimeoutService.cancelTimeoutReminder(taskId);
        return Result.success();
    }

    @Operation(summary = "获取超时配置")
    @GetMapping("/config")
    public Result<Map<String, Object>> getTimeoutConfig(
            @Parameter(description = "流程定义ID") @RequestParam String processDefinitionId,
            @Parameter(description = "任务定义Key") @RequestParam String taskDefinitionKey) {
        Map<String, Object> config =
                workflowTimeoutService.getTimeoutConfig(processDefinitionId, taskDefinitionKey);
        return Result.success(config);
    }

    @Operation(summary = "设置超时配置")
    @PostMapping("/config")
    public Result<Void> setTimeoutConfig(@RequestBody Map<String, Object> request) {
        String processDefinitionId = (String) request.get("processDefinitionId");
        String taskDefinitionKey = (String) request.get("taskDefinitionKey");
        @SuppressWarnings("unchecked")
        Map<String, Object> config = (Map<String, Object>) request.get("config");

        workflowTimeoutService.setTimeoutConfig(processDefinitionId, taskDefinitionKey, config);
        return Result.success();
    }

    @Operation(summary = "批量处理超时任务")
    @PostMapping("/batch-handle")
    public Result<Map<String, Object>> batchHandleTimeoutTasks(
            @Parameter(description = "流程定义ID") @RequestParam String processDefinitionId) {
        Map<String, Object> result =
                workflowTimeoutService.batchHandleTimeoutTasks(processDefinitionId);
        return Result.success(result);
    }

    @Operation(summary = "获取超时统计信息")
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getTimeoutStatistics(
            @Parameter(description = "流程定义ID") @RequestParam String processDefinitionId,
            @Parameter(description = "开始日期") @RequestParam String startDate,
            @Parameter(description = "结束日期") @RequestParam String endDate) {
        Map<String, Object> statistics =
                workflowTimeoutService.getTimeoutStatistics(processDefinitionId, startDate, endDate);
        return Result.success(statistics);
    }
}

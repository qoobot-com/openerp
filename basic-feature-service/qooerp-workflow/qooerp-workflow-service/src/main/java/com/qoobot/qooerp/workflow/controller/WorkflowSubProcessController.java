package com.qoobot.qooerp.workflow.controller;

import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.workflow.service.WorkflowSubProcessService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 子流程控制器
 */
@Tag(name = "子流程管理", description = "子流程的启动、管理、查询等功能")
@RestController
@RequestMapping("/subprocess")
@RequiredArgsConstructor
public class WorkflowSubProcessController {

    private final WorkflowSubProcessService workflowSubProcessService;

    @Operation(summary = "启动子流程")
    @PostMapping("/start")
    public Result<String> startSubProcess(@RequestBody Map<String, Object> request) {
        String parentProcessInstanceId = (String) request.get("parentProcessInstanceId");
        String subProcessDefinitionKey = (String) request.get("subProcessDefinitionKey");
        String callActivityId = (String) request.get("callActivityId");
        String userId = (String) request.get("userId");

        @SuppressWarnings("unchecked")
        Map<String, Object> variables = (Map<String, Object>) request.get("variables");

        String subProcessInstanceId = workflowSubProcessService.startSubProcess(
                parentProcessInstanceId, subProcessDefinitionKey, variables, callActivityId, userId);
        return Result.success(subProcessInstanceId);
    }

    @Operation(summary = "获取父流程的所有子流程")
    @GetMapping("/list")
    public Result<List<Map<String, Object>>> getSubProcesses(
            @Parameter(description = "父流程实例ID") @RequestParam String parentProcessInstanceId) {
        List<Map<String, Object>> subProcesses =
                workflowSubProcessService.getSubProcesses(parentProcessInstanceId);
        return Result.success(subProcesses);
    }

    @Operation(summary = "获取子流程的父流程信息")
    @GetMapping("/parent/{subProcessInstanceId}")
    public Result<Map<String, Object>> getParentProcess(
            @PathVariable String subProcessInstanceId) {
        Map<String, Object> parentProcess =
                workflowSubProcessService.getParentProcess(subProcessInstanceId);
        return Result.success(parentProcess);
    }

    @Operation(summary = "同步子流程变量到父流程")
    @PostMapping("/sync/{subProcessInstanceId}")
    public Result<Void> syncSubProcessVariables(@PathVariable String subProcessInstanceId) {
        workflowSubProcessService.syncSubProcessVariables(subProcessInstanceId);
        return Result.success();
    }

    @Operation(summary = "取消子流程")
    @DeleteMapping("/{subProcessInstanceId}")
    public Result<Void> cancelSubProcess(
            @PathVariable String subProcessInstanceId,
            @RequestParam String reason) {
        workflowSubProcessService.cancelSubProcess(subProcessInstanceId, reason);
        return Result.success();
    }

    @Operation(summary = "获取流程层级结构")
    @GetMapping("/hierarchy/{processInstanceId}")
    public Result<Map<String, Object>> getProcessHierarchy(
            @PathVariable String processInstanceId) {
        Map<String, Object> hierarchy =
                workflowSubProcessService.getProcessHierarchy(processInstanceId);
        return Result.success(hierarchy);
    }

    @Operation(summary = "查询子流程执行历史")
    @GetMapping("/history")
    public Result<List<Map<String, Object>>> getSubProcessHistory(
            @Parameter(description = "父流程实例ID") @RequestParam String parentProcessInstanceId) {
        List<Map<String, Object>> history =
                workflowSubProcessService.getSubProcessHistory(parentProcessInstanceId);
        return Result.success(history);
    }
}

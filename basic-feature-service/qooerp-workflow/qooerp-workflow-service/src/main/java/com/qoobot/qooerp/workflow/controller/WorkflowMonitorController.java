package com.qoobot.qooerp.workflow.controller;

import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.workflow.service.WorkflowMonitorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 流程监控控制器
 */
@Tag(name = "流程监控管理", description = "流程实例监控、统计分析、性能分析")
@RestController
@RequestMapping("/monitor")
@RequiredArgsConstructor
public class WorkflowMonitorController {

    private final WorkflowMonitorService workflowMonitorService;

    @Operation(summary = "查询流程实例详情")
    @GetMapping("/instance/detail/{processInstanceId}")
    public Result<Map<String, Object>> getProcessInstanceDetail(@PathVariable String processInstanceId) {
        Map<String, Object> detail = workflowMonitorService.getProcessInstanceDetail(processInstanceId);
        return Result.success(detail);
    }

    @Operation(summary = "获取流程进度")
    @GetMapping("/progress/{processInstanceId}")
    public Result<Map<String, Object>> getProgress(@PathVariable String processInstanceId) {
        Map<String, Object> progress = workflowMonitorService.getProcessProgress(processInstanceId);
        return Result.success(progress);
    }

    @Operation(summary = "获取流程图")
    @GetMapping(value = "/diagram/{processInstanceId}", produces = "image/png")
    public byte[] getDiagram(@PathVariable String processInstanceId) {
        return workflowMonitorService.getProcessDiagram(processInstanceId);
    }

    @Operation(summary = "获取流程日志")
    @GetMapping("/logs/{processInstanceId}")
    public Result<List<Map<String, Object>>> getLogs(@PathVariable String processInstanceId) {
        List<Map<String, Object>> logs = workflowMonitorService.getProcessLogs(processInstanceId);
        return Result.success(logs);
    }

    @Operation(summary = "流程统计分析")
    @GetMapping("/statistics")
    public Result<Map<String, Object>> statistics(
            @RequestParam String startTime,
            @RequestParam String endTime,
            @RequestParam(required = false) String processDefinitionKey) {
        Map<String, Object> result = workflowMonitorService.statisticsProcess(startTime, endTime, processDefinitionKey);
        return Result.success(result);
    }

    @Operation(summary = "流程性能分析")
    @GetMapping("/performance")
    public Result<Map<String, Object>> analyzePerformance(
            @RequestParam String startTime,
            @RequestParam String endTime,
            @RequestParam(required = false) String processDefinitionKey) {
        Map<String, Object> result = workflowMonitorService.analyzeProcessPerformance(startTime, endTime, processDefinitionKey);
        return Result.success(result);
    }

    @Operation(summary = "查询超时任务列表")
    @GetMapping("/timeout/tasks")
    public Result<Map<String, Object>> listTimeoutTasks(
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Long size) {
        Map<String, Object> result = workflowMonitorService.listTimeoutTasks(current, size);
        return Result.success(result);
    }

    @Operation(summary = "获取流程热点分析")
    @GetMapping("/hotspot")
    public Result<Map<String, Object>> getHotspot(
            @RequestParam String startTime,
            @RequestParam String endTime) {
        Map<String, Object> result = workflowMonitorService.getProcessHotspot(startTime, endTime);
        return Result.success(result);
    }

    @Operation(summary = "获取流程趋势分析")
    @GetMapping("/trend")
    public Result<List<Map<String, Object>>> getTrend(
            @RequestParam String startTime,
            @RequestParam String endTime,
            @RequestParam(required = false) String processDefinitionKey) {
        List<Map<String, Object>> trend = workflowMonitorService.getProcessTrend(startTime, endTime, processDefinitionKey);
        return Result.success(trend);
    }

    @Operation(summary = "查询用户流程统计")
    @GetMapping("/statistics/user/{userId}")
    public Result<Map<String, Object>> statisticsUser(
            @PathVariable String userId,
            @RequestParam String startTime,
            @RequestParam String endTime) {
        Map<String, Object> result = workflowMonitorService.statisticsUserProcess(userId, startTime, endTime);
        return Result.success(result);
    }

    @Operation(summary = "查询部门流程统计")
    @GetMapping("/statistics/dept/{deptId}")
    public Result<Map<String, Object>> statisticsDept(
            @PathVariable String deptId,
            @RequestParam String startTime,
            @RequestParam String endTime) {
        Map<String, Object> result = workflowMonitorService.statisticsDeptProcess(deptId, startTime, endTime);
        return Result.success(result);
    }

    @Operation(summary = "获取监控大屏数据")
    @GetMapping("/dashboard")
    public Result<Map<String, Object>> getDashboard() {
        Map<String, Object> dashboard = workflowMonitorService.getMonitorDashboard();
        return Result.success(dashboard);
    }
}

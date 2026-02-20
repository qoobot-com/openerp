package com.qoobot.qooerp.workflow.controller;

import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.workflow.service.WorkflowGatewayService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 流程网关控制器
 */
@Tag(name = "流程网关管理", description = "条件网关、并行网关等高级网关功能")
@RestController
@RequestMapping("/gateway")
@RequiredArgsConstructor
public class WorkflowGatewayController {

    private final WorkflowGatewayService workflowGatewayService;

    @Operation(summary = "获取条件网关的可用条件")
    @GetMapping("/conditions")
    public Result<List<Map<String, Object>>> getGatewayConditions(
            @Parameter(description = "流程定义ID") @RequestParam String processDefinitionId,
            @Parameter(description = "网关ID") @RequestParam String gatewayId) {
        List<Map<String, Object>> conditions =
                workflowGatewayService.getGatewayConditions(processDefinitionId, gatewayId);
        return Result.success(conditions);
    }

    @Operation(summary = "获取条件网关的决策信息")
    @GetMapping("/decision")
    public Result<Map<String, Object>> getGatewayDecision(
            @Parameter(description = "流程实例ID") @RequestParam String processInstanceId,
            @Parameter(description = "网关ID") @RequestParam String gatewayId) {
        Map<String, Object> decision =
                workflowGatewayService.getGatewayDecision(processInstanceId, gatewayId);
        return Result.success(decision);
    }

    @Operation(summary = "设置条件网关的流程变量")
    @PostMapping("/variables")
    public Result<Void> setGatewayVariables(
            @Parameter(description = "流程实例ID") @RequestParam String processInstanceId,
            @RequestBody Map<String, Object> variables) {
        workflowGatewayService.setGatewayVariables(processInstanceId, variables);
        return Result.success();
    }

    @Operation(summary = "预览条件网关的执行路径")
    @PostMapping("/preview")
    public Result<List<String>> previewGatewayPath(
            @Parameter(description = "流程定义ID") @RequestParam String processDefinitionId,
            @Parameter(description = "网关ID") @RequestParam String gatewayId,
            @RequestBody Map<String, Object> variables) {
        List<String> path =
                workflowGatewayService.previewGatewayPath(processDefinitionId, gatewayId, variables);
        return Result.success(path);
    }

    @Operation(summary = "获取并行网关的分支信息")
    @GetMapping("/parallel/status")
    public Result<Map<String, Object>> getParallelGatewayStatus(
            @Parameter(description = "流程实例ID") @RequestParam String processInstanceId,
            @Parameter(description = "网关ID") @RequestParam String gatewayId) {
        Map<String, Object> status =
                workflowGatewayService.getParallelGatewayStatus(processInstanceId, gatewayId);
        return Result.success(status);
    }

    @Operation(summary = "查询流程实例的所有网关执行历史")
    @GetMapping("/history/{processInstanceId}")
    public Result<List<Map<String, Object>>> getGatewayHistory(
            @PathVariable String processInstanceId) {
        List<Map<String, Object>> history =
                workflowGatewayService.getGatewayHistory(processInstanceId);
        return Result.success(history);
    }
}

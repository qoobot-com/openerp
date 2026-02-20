package com.qoobot.qooerp.workflow.controller;

import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.workflow.service.WorkflowListenerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 流程监听器控制器
 */
@Tag(name = "流程监听器管理", description = "流程监听器的注册、管理、执行等功能")
@RestController
@RequestMapping("/listener")
@RequiredArgsConstructor
public class WorkflowListenerController {

    private final WorkflowListenerService workflowListenerService;

    @Operation(summary = "注册流程监听器")
    @PostMapping("/register")
    public Result<Void> registerListener(@RequestBody Map<String, Object> request) {
        String processDefinitionId = (String) request.get("processDefinitionId");
        String eventType = (String) request.get("eventType");
        String listenerClass = (String) request.get("listenerClass");
        String listenerType = (String) request.get("listenerType");
        String description = (String) request.get("description");

        workflowListenerService.registerListener(processDefinitionId, eventType, listenerClass,
                listenerType, description);
        return Result.success();
    }

    @Operation(summary = "注销流程监听器")
    @DeleteMapping("/{listenerId}")
    public Result<Void> unregisterListener(@PathVariable String listenerId) {
        workflowListenerService.unregisterListener(listenerId);
        return Result.success();
    }

    @Operation(summary = "获取流程的所有监听器")
    @GetMapping("/list")
    public Result<List<Map<String, Object>>> getProcessListeners(
            @Parameter(description = "流程定义ID") @RequestParam String processDefinitionId) {
        List<Map<String, Object>> listeners =
                workflowListenerService.getProcessListeners(processDefinitionId);
        return Result.success(listeners);
    }

    @Operation(summary = "触发监听器执行")
    @PostMapping("/trigger")
    public Result<Void> triggerListener(@RequestBody Map<String, Object> request) {
        String processInstanceId = (String) request.get("processInstanceId");
        String eventType = (String) request.get("eventType");
        @SuppressWarnings("unchecked")
        Map<String, Object> variables = (Map<String, Object>) request.get("variables");

        workflowListenerService.triggerListener(processInstanceId, eventType, variables);
        return Result.success();
    }

    @Operation(summary = "获取监听器执行历史")
    @GetMapping("/history/{processInstanceId}")
    public Result<List<Map<String, Object>>> getListenerExecutionHistory(
            @PathVariable String processInstanceId) {
        List<Map<String, Object>> history =
                workflowListenerService.getListenerExecutionHistory(processInstanceId);
        return Result.success(history);
    }
}

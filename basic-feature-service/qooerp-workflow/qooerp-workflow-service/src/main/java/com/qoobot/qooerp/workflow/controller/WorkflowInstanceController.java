package com.qoobot.qooerp.workflow.controller;

import com.qoobot.qooerp.common.response.PageResult;
import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.workflow.dto.ProcessInstanceDTO;
import com.qoobot.qooerp.workflow.service.WorkflowInstanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 流程实例控制器
 */
@Tag(name = "流程实例管理", description = "流程实例的启动、查询、取消等操作")
@RestController
@RequestMapping("/instance")
@RequiredArgsConstructor
public class WorkflowInstanceController {

    private final WorkflowInstanceService workflowInstanceService;

    @Operation(summary = "启动流程实例")
    @PostMapping("/start")
    public Result<String> start(@RequestBody Map<String, Object> request) {
        String processDefinitionKey = (String) request.get("processDefinitionKey");
        String businessKey = (String) request.get("businessKey");
        @SuppressWarnings("unchecked")
        Map<String, Object> variables = (Map<String, Object>) request.get("variables");
        String startUserId = (String) request.get("startUserId");
        String processInstanceId = workflowInstanceService.startProcess(
                processDefinitionKey, businessKey, variables, startUserId);
        return Result.success(processInstanceId);
    }

    @Operation(summary = "完成流程实例")
    @PostMapping("/complete/{processInstanceId}")
    public Result<Void> complete(@PathVariable String processInstanceId) {
        workflowInstanceService.completeProcess(processInstanceId);
        return Result.success();
    }

    @Operation(summary = "取消流程实例")
    @PostMapping("/cancel/{processInstanceId}")
    public Result<Void> cancel(
            @PathVariable String processInstanceId,
            @RequestParam(value = "reason", required = false) String reason) {
        workflowInstanceService.cancelProcess(processInstanceId, reason);
        return Result.success();
    }

    @Operation(summary = "挂起流程实例")
    @PostMapping("/suspend/{processInstanceId}")
    public Result<Void> suspend(@PathVariable String processInstanceId) {
        workflowInstanceService.suspendProcess(processInstanceId);
        return Result.success();
    }

    @Operation(summary = "恢复流程实例")
    @PostMapping("/activate/{processInstanceId}")
    public Result<Void> activate(@PathVariable String processInstanceId) {
        workflowInstanceService.activateProcess(processInstanceId);
        return Result.success();
    }

    @Operation(summary = "撤回流程实例")
    @PostMapping("/withdraw/{processInstanceId}")
    public Result<Void> withdraw(
            @PathVariable String processInstanceId,
            @RequestParam String taskId,
            @RequestParam(value = "reason", required = false) String reason) {
        workflowInstanceService.withdrawProcess(processInstanceId, taskId, reason);
        return Result.success();
    }

    @Operation(summary = "转办流程实例")
    @PostMapping("/transfer/{processInstanceId}")
    public Result<Void> transfer(
            @PathVariable String processInstanceId,
            @RequestParam String targetUserId,
            @RequestParam(value = "reason", required = false) String reason) {
        workflowInstanceService.transferProcess(processInstanceId, targetUserId, reason);
        return Result.success();
    }

    @Operation(summary = "查询流程实例详情")
    @GetMapping("/detail/{processInstanceId}")
    public Result<ProcessInstanceDTO> getDetail(@PathVariable String processInstanceId) {
        ProcessInstanceDTO dto = workflowInstanceService.getProcessInstance(processInstanceId);
        return Result.success(dto);
    }

    @Operation(summary = "根据业务Key查询流程实例")
    @GetMapping("/business/{businessKey}")
    public Result<ProcessInstanceDTO> getByBusinessKey(@PathVariable String businessKey) {
        ProcessInstanceDTO dto = workflowInstanceService.getProcessInstanceByBusinessKey(businessKey);
        return Result.success(dto);
    }

    @Operation(summary = "分页查询流程实例列表")
    @GetMapping("/page")
    public Result<PageResult<ProcessInstanceDTO>> page(
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Long size,
            @Parameter(description = "流程定义Key") @RequestParam(required = false) String processDefinitionKey,
            @Parameter(description = "流程状态") @RequestParam(required = false) String status,
            @Parameter(description = "发起人ID") @RequestParam(required = false) String startUserId) {
        PageResult<ProcessInstanceDTO> result = workflowInstanceService.pageProcessInstances(
                current, size, processDefinitionKey, status, startUserId);
        return Result.success(result);
    }

    @Operation(summary = "获取流程进度")
    @GetMapping("/progress/{processInstanceId}")
    public Result<Map<String, Object>> getProgress(@PathVariable String processInstanceId) {
        Map<String, Object> progress = workflowInstanceService.getProcessProgress(processInstanceId);
        return Result.success(progress);
    }

    @Operation(summary = "获取流程图")
    @GetMapping(value = "/diagram/{processInstanceId}", produces = "image/png")
    public byte[] getDiagram(@PathVariable String processInstanceId) {
        return workflowInstanceService.getProcessDiagram(processInstanceId);
    }

    @Operation(summary = "获取流程日志")
    @GetMapping("/logs/{processInstanceId}")
    public Result<Map<String, Object>> getLogs(@PathVariable String processInstanceId) {
        Map<String, Object> logs = workflowInstanceService.getProcessLogs(processInstanceId);
        return Result.success(logs);
    }

    @Operation(summary = "删除流程实例")
    @DeleteMapping("/delete/{processInstanceId}")
    public Result<Void> delete(
            @PathVariable String processInstanceId,
            @Parameter(description = "是否级联删除") @RequestParam(defaultValue = "false") boolean cascade) {
        workflowInstanceService.deleteProcess(processInstanceId, cascade);
        return Result.success();
    }
}

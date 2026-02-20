package com.qoobot.qooerp.workflow.controller;

import com.qoobot.qooerp.common.response.PageResult;
import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.workflow.dto.TaskDTO;
import com.qoobot.qooerp.workflow.service.WorkflowTaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 任务控制器
 */
@Tag(name = "任务管理", description = "待办、已办任务的查询和审批操作")
@RestController
@RequestMapping("/task")
@RequiredArgsConstructor
public class WorkflowTaskController {

    private final WorkflowTaskService workflowTaskService;

    @Operation(summary = "查询待办任务列表")
    @GetMapping("/todo/list")
    public Result<List<TaskDTO>> listTodo(@RequestParam String userId) {
        List<TaskDTO> list = workflowTaskService.listTodoTasks(userId);
        return Result.success(list);
    }

    @Operation(summary = "分页查询待办任务")
    @GetMapping("/todo/page")
    public Result<PageResult<TaskDTO>> pageTodo(
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Long size,
            @Parameter(description = "用户ID") @RequestParam String userId,
            @Parameter(description = "流程定义Key") @RequestParam(required = false) String processDefinitionKey,
            @Parameter(description = "任务名称") @RequestParam(required = false) String taskName) {
        PageResult<TaskDTO> result = workflowTaskService.pageTodoTasks(
                current, size, userId, processDefinitionKey, taskName);
        return Result.success(result);
    }

    @Operation(summary = "查询已办任务列表")
    @GetMapping("/done/list")
    public Result<List<TaskDTO>> listDone(@RequestParam String userId) {
        List<TaskDTO> list = workflowTaskService.listDoneTasks(userId);
        return Result.success(list);
    }

    @Operation(summary = "分页查询已办任务")
    @GetMapping("/done/page")
    public Result<PageResult<TaskDTO>> pageDone(
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Long size,
            @Parameter(description = "用户ID") @RequestParam String userId,
            @Parameter(description = "流程定义Key") @RequestParam(required = false) String processDefinitionKey,
            @Parameter(description = "任务名称") @RequestParam(required = false) String taskName) {
        PageResult<TaskDTO> result = workflowTaskService.pageDoneTasks(
                current, size, userId, processDefinitionKey, taskName);
        return Result.success(result);
    }

    @Operation(summary = "查询抄送任务列表")
    @GetMapping("/cc/list")
    public Result<List<TaskDTO>> listCc(@RequestParam String userId) {
        List<TaskDTO> list = workflowTaskService.listCcTasks(userId);
        return Result.success(list);
    }

    @Operation(summary = "查询任务详情")
    @GetMapping("/detail/{taskId}")
    public Result<TaskDTO> getDetail(@PathVariable String taskId) {
        TaskDTO dto = workflowTaskService.getTask(taskId);
        return Result.success(dto);
    }

    @Operation(summary = "审批通过")
    @PostMapping("/approve/{taskId}")
    public Result<Void> approve(
            @PathVariable String taskId,
            @RequestBody Map<String, Object> request) {
        String comment = (String) request.get("comment");
        @SuppressWarnings("unchecked")
        Map<String, Object> variables = (Map<String, Object>) request.get("variables");
        String userId = (String) request.get("userId");
        workflowTaskService.approveTask(taskId, comment, variables, userId);
        return Result.success();
    }

    @Operation(summary = "审批驳回")
    @PostMapping("/reject/{taskId}")
    public Result<Void> reject(
            @PathVariable String taskId,
            @RequestBody Map<String, Object> request) {
        String comment = (String) request.get("comment");
        String userId = (String) request.get("userId");
        workflowTaskService.rejectTask(taskId, comment, userId);
        return Result.success();
    }

    @Operation(summary = "任务转派")
    @PostMapping("/delegate/{taskId}")
    public Result<Void> delegate(
            @PathVariable String taskId,
            @RequestBody Map<String, Object> request) {
        String targetUserId = (String) request.get("targetUserId");
        String comment = (String) request.get("comment");
        String userId = (String) request.get("userId");
        workflowTaskService.delegateTask(taskId, targetUserId, comment, userId);
        return Result.success();
    }

    @Operation(summary = "任务委派")
    @PostMapping("/assign/{taskId}")
    public Result<Void> assign(
            @PathVariable String taskId,
            @RequestBody Map<String, Object> request) {
        String targetUserId = (String) request.get("targetUserId");
        String comment = (String) request.get("comment");
        String userId = (String) request.get("userId");
        workflowTaskService.assignTask(taskId, targetUserId, comment, userId);
        return Result.success();
    }

    @Operation(summary = "撤回任务")
    @PostMapping("/withdraw/{taskId}")
    public Result<Void> withdraw(
            @PathVariable String taskId,
            @RequestBody Map<String, Object> request) {
        String reason = (String) request.get("reason");
        String userId = (String) request.get("userId");
        workflowTaskService.withdrawTask(taskId, reason, userId);
        return Result.success();
    }

    @Operation(summary = "加签（向前）")
    @PostMapping("/add-sign/before/{taskId}")
    public Result<Void> addSignBefore(
            @PathVariable String taskId,
            @RequestBody Map<String, Object> request) {
        @SuppressWarnings("unchecked")
        List<String> userIds = (List<String>) request.get("userIds");
        String comment = (String) request.get("comment");
        String userId = (String) request.get("userId");
        workflowTaskService.addSignBefore(taskId, userIds, comment, userId);
        return Result.success();
    }

    @Operation(summary = "加签（向后）")
    @PostMapping("/add-sign/after/{taskId}")
    public Result<Void> addSignAfter(
            @PathVariable String taskId,
            @RequestBody Map<String, Object> request) {
        @SuppressWarnings("unchecked")
        List<String> userIds = (List<String>) request.get("userIds");
        String comment = (String) request.get("comment");
        String userId = (String) request.get("userId");
        workflowTaskService.addSignAfter(taskId, userIds, comment, userId);
        return Result.success();
    }

    @Operation(summary = "减签")
    @PostMapping("/remove-sign/{taskId}")
    public Result<Void> removeSign(
            @PathVariable String taskId,
            @RequestBody Map<String, Object> request) {
        @SuppressWarnings("unchecked")
        List<String> userIds = (List<String>) request.get("userIds");
        String userId = (String) request.get("userId");
        workflowTaskService.removeSign(taskId, userIds, userId);
        return Result.success();
    }

    @Operation(summary = "抄送任务")
    @PostMapping("/cc/{taskId}")
    public Result<Void> cc(
            @PathVariable String taskId,
            @RequestBody Map<String, Object> request) {
        @SuppressWarnings("unchecked")
        List<String> userIds = (List<String>) request.get("userIds");
        String userId = (String) request.get("userId");
        workflowTaskService.ccTask(taskId, userIds, userId);
        return Result.success();
    }

    @Operation(summary = "获取任务表单")
    @GetMapping("/form/{taskId}")
    public Result<Map<String, Object>> getTaskForm(@PathVariable String taskId) {
        Map<String, Object> formData = workflowTaskService.getTaskForm(taskId);
        return Result.success(formData);
    }

    @Operation(summary = "保存任务表单")
    @PostMapping("/form/save/{taskId}")
    public Result<Void> saveTaskForm(
            @PathVariable String taskId,
            @RequestBody Map<String, Object> request) {
        @SuppressWarnings("unchecked")
        Map<String, Object> formData = (Map<String, Object>) request.get("formData");
        String userId = (String) request.get("userId");
        workflowTaskService.saveTaskForm(taskId, formData, userId);
        return Result.success();
    }

    @Operation(summary = "查询任务历史")
    @GetMapping("/history/{taskId}")
    public Result<List<Map<String, Object>>> listHistory(@PathVariable String taskId) {
        List<Map<String, Object>> history = workflowTaskService.listTaskHistory(taskId);
        return Result.success(history);
    }

    @Operation(summary = "设置任务到期时间")
    @PostMapping("/due-date/{taskId}")
    public Result<Void> setDueDate(
            @PathVariable String taskId,
            @RequestParam String dueDate) {
        workflowTaskService.setTaskDueDate(taskId, dueDate);
        return Result.success();
    }

    @Operation(summary = "会签任务审批")
    @PostMapping("/multi-instance/approve/{taskId}")
    public Result<Void> approveMultiInstance(
            @PathVariable String taskId,
            @RequestBody Map<String, Object> request) {
        String comment = (String) request.get("comment");
        String userId = (String) request.get("userId");
        workflowTaskService.approveMultiInstanceTask(taskId, comment, userId);
        return Result.success();
    }

    @Operation(summary = "或签任务审批")
    @PostMapping("/or-sign/approve/{taskId}")
    public Result<Void> approveOrSign(
            @PathVariable String taskId,
            @RequestBody Map<String, Object> request) {
        String comment = (String) request.get("comment");
        String userId = (String) request.get("userId");
        workflowTaskService.approveOrSignTask(taskId, comment, userId);
        return Result.success();
    }

    @Operation(summary = "获取会签任务状态")
    @GetMapping("/multi-instance/status/{taskId}")
    public Result<Map<String, Object>> getMultiInstanceStatus(@PathVariable String taskId) {
        Map<String, Object> status = workflowTaskService.getMultiInstanceTaskStatus(taskId);
        return Result.success(status);
    }
}

package com.qoobot.qooerp.workflow.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.common.response.PageResult;
import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.workflow.entity.WorkflowNotification;
import com.qoobot.qooerp.workflow.service.WorkflowNotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 流程通知控制器
 */
@Tag(name = "流程通知管理", description = "流程消息通知的发送和查询")
@RestController
@RequestMapping("/notification")
@RequiredArgsConstructor
public class WorkflowNotificationController {

    private final WorkflowNotificationService workflowNotificationService;

    @Operation(summary = "标记通知已读")
    @PostMapping("/read/{notificationId}")
    public Result<Void> markAsRead(@PathVariable String notificationId) {
        workflowNotificationService.markAsRead(notificationId);
        return Result.success();
    }

    @Operation(summary = "批量标记已读")
    @PostMapping("/read/batch")
    public Result<Void> batchMarkAsRead(@RequestBody List<String> notificationIds) {
        workflowNotificationService.batchMarkAsRead(notificationIds);
        return Result.success();
    }

    @Operation(summary = "查询用户通知列表")
    @GetMapping("/list")
    public Result<List<WorkflowNotification>> listUser(
            @RequestParam String userId,
            @RequestParam(required = false) Integer isRead) {
        List<WorkflowNotification> list = workflowNotificationService.listUserNotifications(userId, isRead);
        return Result.success(list);
    }

    @Operation(summary = "分页查询用户通知")
    @GetMapping("/page")
    public Result<Page<WorkflowNotification>> pageUser(
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Long size,
            @Parameter(description = "用户ID") @RequestParam String userId,
            @Parameter(description = "是否已读") @RequestParam(required = false) Integer isRead) {
        Page<WorkflowNotification> page = workflowNotificationService.pageUserNotifications(current, size, userId, isRead);
        return Result.success(page);
    }

    @Operation(summary = "查询未读通知数量")
    @GetMapping("/unread/count")
    public Result<Long> getUnreadCount(@RequestParam String userId) {
        Long count = workflowNotificationService.getUnreadCount(userId);
        return Result.success(count);
    }

    @Operation(summary = "删除通知")
    @DeleteMapping("/delete/{notificationId}")
    public Result<Void> delete(@PathVariable String notificationId) {
        workflowNotificationService.deleteNotification(notificationId);
        return Result.success();
    }

    @Operation(summary = "批量删除通知")
    @DeleteMapping("/delete/batch")
    public Result<Void> batchDelete(@RequestBody List<String> notificationIds) {
        workflowNotificationService.batchDeleteNotifications(notificationIds);
        return Result.success();
    }

    @Operation(summary = "查询通知详情")
    @GetMapping("/detail/{notificationId}")
    public Result<WorkflowNotification> getDetail(@PathVariable String notificationId) {
        WorkflowNotification notification = workflowNotificationService.getNotification(notificationId);
        return Result.success(notification);
    }
}

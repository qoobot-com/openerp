package com.qoobot.qooerp.workflow.controller;

import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.workflow.service.WorkflowNotificationChannelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 通知方式集成控制器
 */
@Tag(name = "通知方式集成", description = "支持邮件、短信、企业微信、钉钉等多种通知渠道")
@RestController
@RequestMapping("/notification/channel")
@RequiredArgsConstructor
public class WorkflowNotificationChannelController {

    private final WorkflowNotificationChannelService notificationChannelService;

    @Operation(summary = "发送通知")
    @PostMapping("/send")
    public Result<Map<String, Object>> sendNotification(@RequestBody Map<String, Object> request) {
        String channel = (String) request.get("channel");
        @SuppressWarnings("unchecked")
        List<String> receivers = (List<String>) request.get("receivers");
        String title = (String) request.get("title");
        String content = (String) request.get("content");
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) request.getOrDefault("data", new HashMap<>());

        Map<String, Object> result =
                notificationChannelService.sendNotification(channel, receivers, title, content, data);
        return Result.success(result);
    }

    @Operation(summary = "批量发送通知")
    @PostMapping("/batch-send")
    public Result<Map<String, Object>> batchSendNotifications(
            @RequestBody List<Map<String, Object>> notifications) {
        Map<String, Object> result =
                notificationChannelService.batchSendNotifications(notifications);
        return Result.success(result);
    }

    @Operation(summary = "获取支持的通知渠道")
    @GetMapping("/channels")
    public Result<List<Map<String, Object>>> getSupportedChannels() {
        List<Map<String, Object>> channels = notificationChannelService.getSupportedChannels();
        return Result.success(channels);
    }

    @Operation(summary = "获取渠道配置")
    @GetMapping("/config/{channel}")
    public Result<Map<String, Object>> getChannelConfig(@PathVariable String channel) {
        Map<String, Object> config = notificationChannelService.getChannelConfig(channel);
        return Result.success(config);
    }

    @Operation(summary = "设置渠道配置")
    @PostMapping("/config/{channel}")
    public Result<Void> setChannelConfig(
            @PathVariable String channel,
            @RequestBody Map<String, Object> config) {
        notificationChannelService.setChannelConfig(channel, config);
        return Result.success();
    }

    @Operation(summary = "测试通知发送")
    @PostMapping("/test/{channel}")
    public Result<Map<String, Object>> testChannel(
            @PathVariable String channel,
            @RequestParam String receiver) {
        Map<String, Object> result = notificationChannelService.testChannel(channel, receiver);
        return Result.success(result);
    }

    @Operation(summary = "获取通知发送历史")
    @GetMapping("/history/{channel}")
    public Result<List<Map<String, Object>>> getNotificationHistory(
            @PathVariable String channel,
            @Parameter(description = "限制数量") @RequestParam(defaultValue = "100") int limit) {
        List<Map<String, Object>> history =
                notificationChannelService.getNotificationHistory(channel, limit);
        return Result.success(history);
    }

    @Operation(summary = "获取通知发送统计")
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getNotificationStatistics(
            @Parameter(description = "开始日期") @RequestParam String startDate,
            @Parameter(description = "结束日期") @RequestParam String endDate) {
        Map<String, Object> statistics =
                notificationChannelService.getNotificationStatistics(startDate, endDate);
        return Result.success(statistics);
    }
}

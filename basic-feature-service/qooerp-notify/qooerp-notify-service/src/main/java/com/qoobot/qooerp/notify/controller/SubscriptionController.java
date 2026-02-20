package com.qoobot.qooerp.notify.controller;

import com.qoobot.qooerp.notify.common.Result;
import com.qoobot.qooerp.notify.dto.DndSettingDTO;
import com.qoobot.qooerp.notify.dto.SubscriptionDTO;
import com.qoobot.qooerp.notify.service.SubscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 订阅管理控制器
 */
@Tag(name = "订阅管理", description = "订阅管理相关接口")
@RestController
@RequestMapping("/subscription")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @Operation(summary = "订阅通知")
    @PostMapping("/subscribe")
    public Result<Void> subscribe(@Valid @RequestBody SubscriptionDTO dto) {
        subscriptionService.subscribe(dto);
        return Result.success();
    }

    @Operation(summary = "取消订阅")
    @PostMapping("/unsubscribe")
    public Result<Void> unsubscribe(
            @RequestParam Long userId,
            @RequestParam String channelType
    ) {
        subscriptionService.unsubscribe(userId, channelType);
        return Result.success();
    }

    @Operation(summary = "获取用户订阅列表")
    @GetMapping("/user/{userId}")
    public Result<List<Map<String, Object>>> getUserSubscriptions(@PathVariable Long userId) {
        List<Map<String, Object>> subscriptions = subscriptionService.getUserSubscriptions(userId);
        return Result.success(subscriptions);
    }

    @Operation(summary = "设置免打扰")
    @PostMapping("/setDND")
    public Result<Void> setDND(@Valid @RequestBody DndSettingDTO dto) {
        subscriptionService.setDND(dto);
        return Result.success();
    }
}

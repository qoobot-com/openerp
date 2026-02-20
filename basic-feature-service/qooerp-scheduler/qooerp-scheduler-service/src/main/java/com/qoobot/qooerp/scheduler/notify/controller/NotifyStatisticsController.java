package com.qoobot.qooerp.scheduler.notify.controller;

import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.scheduler.notify.service.impl.NotifyStatisticsServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 报警统计控制器
 */
@RestController
@RequestMapping("/api/scheduler/notifies/statistics")
@Tag(name = "报警统计", description = "报警统计相关接口")
@RequiredArgsConstructor
public class NotifyStatisticsController {

    private final NotifyStatisticsServiceImpl notifyStatisticsService;

    @GetMapping("/count")
    @Operation(summary = "报警次数统计")
    public Result<Map<String, Long>> getNotifyCountStatistics() {
        Map<String, Long> statistics = notifyStatisticsService.getNotifyCountStatistics();
        return Result.success(statistics);
    }

    @GetMapping("/type")
    @Operation(summary = "报警类型统计")
    public Result<Map<String, Long>> getNotifyTypeStatistics() {
        Map<String, Long> statistics = notifyStatisticsService.getNotifyTypeStatistics();
        return Result.success(statistics);
    }

    @GetMapping("/level")
    @Operation(summary = "报警级别统计")
    public Result<Map<String, Long>> getNotifyLevelStatistics() {
        Map<String, Long> statistics = notifyStatisticsService.getNotifyLevelStatistics();
        return Result.success(statistics);
    }

    @GetMapping("/today")
    @Operation(summary = "今日报警统计")
    public Result<Map<String, Long>> getTodayNotifyStatistics() {
        Map<String, Long> statistics = notifyStatisticsService.getTodayNotifyStatistics();
        return Result.success(statistics);
    }

    @GetMapping("/pending-count")
    @Operation(summary = "未处理报警数量")
    public Result<Long> getPendingNotifyCount() {
        Long count = notifyStatisticsService.getPendingNotifyCount();
        return Result.success(count);
    }

    @GetMapping("/unsent-count")
    @Operation(summary = "待发送报警数量")
    public Result<Long> getUnsentNotifyCount() {
        Long count = notifyStatisticsService.getUnsentNotifyCount();
        return Result.success(count);
    }
}

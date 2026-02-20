package com.qoobot.qooerp.scheduler.log.controller;

import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.scheduler.log.dto.JobStatisticsDTO;
import com.qoobot.qooerp.scheduler.log.dto.MonitorStatisticsDTO;
import com.qoobot.qooerp.scheduler.log.service.impl.StatisticsServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 统计控制器
 */
@RestController
@RequestMapping("/api/scheduler/statistics")
@Tag(name = "统计分析", description = "统计分析相关接口")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsServiceImpl statisticsService;

    @GetMapping("/job/{jobId}")
    @Operation(summary = "获取任务统计")
    public Result<JobStatisticsDTO> getJobStatistics(@PathVariable Long jobId) {
        JobStatisticsDTO statistics = statisticsService.getJobStatistics(jobId);
        return Result.success(statistics);
    }

    @GetMapping("/monitor")
    @Operation(summary = "获取监控统计")
    public Result<MonitorStatisticsDTO> getMonitorStatistics() {
        MonitorStatisticsDTO statistics = statisticsService.getMonitorStatistics();
        return Result.success(statistics);
    }
}

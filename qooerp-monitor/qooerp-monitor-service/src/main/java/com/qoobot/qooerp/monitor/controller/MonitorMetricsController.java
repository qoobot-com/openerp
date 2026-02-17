package com.qoobot.qooerp.monitor.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.monitor.dto.*;
import com.qoobot.qooerp.monitor.service.MonitorMetricsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/monitor/metrics")
@RequiredArgsConstructor
public class MonitorMetricsController {

    private final MonitorMetricsService metricsService;

    @GetMapping("/current")
    public Result<MonitorMetricsDTO> getCurrentMetrics(@RequestParam String serviceName) {
        MonitorMetricsDTO dto = metricsService.getCurrentMetrics(serviceName);
        return Result.success(dto);
    }

    @PostMapping("/history")
    public Result<Page<MonitorMetricsDTO>> getHistoryMetrics(@RequestBody MetricsQueryDTO dto) {
        Page<MonitorMetricsDTO> page = metricsService.getHistoryMetrics(dto);
        return Result.success(page);
    }

    @GetMapping("/summary")
    public Result<MetricsSummaryDTO> getSummary(MetricsSummaryQueryDTO dto) {
        MetricsSummaryDTO summary = metricsService.getSummary(dto);
        return Result.success(summary);
    }

    @GetMapping("/trend")
    public Result<List<MetricsTrendDTO>> getTrend(MetricsTrendQueryDTO dto) {
        List<MetricsTrendDTO> trend = metricsService.getTrend(dto);
        return Result.success(trend);
    }
}

package com.qoobot.qooerp.monitor.controller;

import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.monitor.dto.*;
import com.qoobot.qooerp.monitor.service.MonitorDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/monitor/dashboard")
@RequiredArgsConstructor
public class MonitorDashboardController {

    private final MonitorDashboardService dashboardService;

    @GetMapping("/overview")
    public Result<DashboardOverviewDTO> getOverview() {
        DashboardOverviewDTO overview = dashboardService.getOverview();
        return Result.success(overview);
    }

    @GetMapping("/services-status")
    public Result<List<ServiceStatusDTO>> getServicesStatus() {
        List<ServiceStatusDTO> status = dashboardService.getServicesStatus();
        return Result.success(status);
    }

    @GetMapping("/realtime-alerts")
    public Result<List<MonitorAlertDTO>> getRealtimeAlerts() {
        List<MonitorAlertDTO> alerts = dashboardService.getRealtimeAlerts();
        return Result.success(alerts);
    }

    @GetMapping("/metrics-trend")
    public Result<List<MetricsTrendDTO>> getMetricsTrend(MetricsTrendQueryDTO dto) {
        List<MetricsTrendDTO> trend = dashboardService.getMetricsTrend(dto);
        return Result.success(trend);
    }
}

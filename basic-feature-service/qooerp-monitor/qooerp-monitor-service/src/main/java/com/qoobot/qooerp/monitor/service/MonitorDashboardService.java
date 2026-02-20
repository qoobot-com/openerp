package com.qoobot.qooerp.monitor.service;

import com.qoobot.qooerp.monitor.dto.*;

import java.util.List;

public interface MonitorDashboardService {
    DashboardOverviewDTO getOverview();

    List<ServiceStatusDTO> getServicesStatus();

    List<MonitorAlertDTO> getRealtimeAlerts();

    List<MetricsTrendDTO> getMetricsTrend(MetricsTrendQueryDTO dto);
}

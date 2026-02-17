package com.qoobot.qooerp.monitor.dto;

import lombok.Data;

import java.util.List;

@Data
public class DashboardOverviewDTO {
    private Integer totalServices;
    private Integer onlineServices;
    private Integer offlineServices;
    private Integer unhealthyServices;
    private Long totalAlerts;
    private Long pendingAlerts;
    private Long handledAlerts;
    private Long closedAlerts;
    private List<ServiceStatusDTO> servicesStatus;
    private List<MonitorAlertDTO> recentAlerts;
    private DashboardMetricsDTO metrics;
}

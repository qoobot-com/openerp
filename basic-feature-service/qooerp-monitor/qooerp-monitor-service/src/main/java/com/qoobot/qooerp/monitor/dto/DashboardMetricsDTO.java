package com.qoobot.qooerp.monitor.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class DashboardMetricsDTO {
    private BigDecimal cpuUsage;
    private BigDecimal memoryUsage;
    private BigDecimal diskUsage;
    private BigDecimal networkIn;
    private BigDecimal networkOut;
    private List<MetricsTrendDTO.TrendData> cpuTrend;
    private List<MetricsTrendDTO.TrendData> memoryTrend;
}

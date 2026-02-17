package com.qoobot.qooerp.monitor.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
public class MetricsSummaryDTO {
    private String serviceName;
    private BigDecimal cpuUsageAvg;
    private BigDecimal memoryUsageAvg;
    private BigDecimal diskUsageAvg;
    private BigDecimal networkInAvg;
    private BigDecimal networkOutAvg;
    private Map<String, BigDecimal> jvmMetricsAvg;
}

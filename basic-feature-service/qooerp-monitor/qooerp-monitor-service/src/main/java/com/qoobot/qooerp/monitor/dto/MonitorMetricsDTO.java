package com.qoobot.qooerp.monitor.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class MonitorMetricsDTO {
    private String serviceName;
    private List<MetricItem> cpuMetrics;
    private List<MetricItem> memoryMetrics;
    private List<MetricItem> diskMetrics;
    private List<MetricItem> networkMetrics;
    private List<MetricItem> jvmMetrics;
    private LocalDateTime collectTime;

    @Data
    public static class MetricItem {
        private String metricName;
        private BigDecimal metricValue;
        private String metricUnit;
    }
}

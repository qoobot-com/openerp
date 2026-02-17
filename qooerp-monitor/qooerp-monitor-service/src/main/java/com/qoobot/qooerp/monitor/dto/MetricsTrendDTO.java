package com.qoobot.qooerp.monitor.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class MetricsTrendDTO {
    private String metricName;
    private List<TrendData> trendList;

    @Data
    public static class TrendData {
        private LocalDateTime timestamp;
        private BigDecimal value;
    }
}

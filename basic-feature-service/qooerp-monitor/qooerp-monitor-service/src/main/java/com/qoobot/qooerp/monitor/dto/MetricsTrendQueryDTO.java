package com.qoobot.qooerp.monitor.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MetricsTrendQueryDTO {
    private String serviceName;
    private String metricName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String interval;
}

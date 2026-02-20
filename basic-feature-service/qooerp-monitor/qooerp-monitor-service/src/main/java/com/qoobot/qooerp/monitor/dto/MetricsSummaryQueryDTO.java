package com.qoobot.qooerp.monitor.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MetricsSummaryQueryDTO {
    private String serviceName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}

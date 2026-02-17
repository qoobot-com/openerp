package com.qoobot.qooerp.monitor.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AlertStatisticsQueryDTO {
    private String serviceName;
    private String alertType;
    private Integer alertLevel;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}

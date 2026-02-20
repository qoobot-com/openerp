package com.qoobot.qooerp.monitor.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MonitorLogDTO {
    private Long id;
    private String serviceName;
    private String logType;
    private String logLevel;
    private String logContent;
    private LocalDateTime logTime;
    private String ipAddress;
    private Long userId;
}

package com.qoobot.qooerp.monitor.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ServiceStatusDTO {
    private Long id;
    private String serviceName;
    private Integer status;
    private Integer healthStatus;
    private LocalDateTime lastCheckTime;
}

package com.qoobot.qooerp.monitor.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MonitorServiceDTO {
    private Long id;
    private String serviceName;
    private String serviceAddress;
    private Integer servicePort;
    private Integer status;
    private Integer healthStatus;
    private LocalDateTime lastCheckTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

package com.qoobot.qooerp.monitor.dto;

import lombok.Data;

@Data
public class MonitorServiceUpdateDTO {
    private String serviceName;
    private String serviceAddress;
    private Integer servicePort;
    private Integer status;
    private Integer healthStatus;
}

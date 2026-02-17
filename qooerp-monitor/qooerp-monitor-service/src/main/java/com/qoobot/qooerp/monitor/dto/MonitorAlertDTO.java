package com.qoobot.qooerp.monitor.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MonitorAlertDTO {
    private Long id;
    private String serviceName;
    private String alertType;
    private Integer alertLevel;
    private String alertContent;
    private LocalDateTime alertTime;
    private Integer status;
    private Long handlerId;
    private LocalDateTime handleTime;
    private String handleRemark;
    private LocalDateTime createTime;
}

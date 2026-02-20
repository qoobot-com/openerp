package com.qoobot.qooerp.monitor.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AlertChannelDTO {
    private Long id;
    private String channelName;
    private String channelType;
    private String channelConfig;
    private Boolean enabled;
    private String description;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

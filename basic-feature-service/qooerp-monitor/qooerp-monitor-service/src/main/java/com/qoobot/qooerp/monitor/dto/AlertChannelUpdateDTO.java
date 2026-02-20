package com.qoobot.qooerp.monitor.dto;

import lombok.Data;

@Data
public class AlertChannelUpdateDTO {
    private String channelName;
    private String channelType;
    private String channelConfig;
    private Boolean enabled;
    private String description;
}

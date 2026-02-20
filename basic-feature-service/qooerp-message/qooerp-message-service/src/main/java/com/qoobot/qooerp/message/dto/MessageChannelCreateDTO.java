package com.qoobot.qooerp.message.dto;

import lombok.Data;

@Data
public class MessageChannelCreateDTO {
    private String channelCode;
    private String channelName;
    private String channelType;
    private String config;
}

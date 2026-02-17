package com.qoobot.qooerp.message.dto;

import lombok.Data;

@Data
public class MessageChannelQueryDTO {
    private Long current;
    private Long size;
    private String channelCode;
    private String channelName;
    private String channelType;
    private Integer status;
}

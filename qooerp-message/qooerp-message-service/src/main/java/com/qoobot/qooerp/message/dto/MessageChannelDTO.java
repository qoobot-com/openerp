package com.qoobot.qooerp.message.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MessageChannelDTO {
    private Long id;
    private String channelCode;
    private String channelName;
    private String channelType;
    private String config;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

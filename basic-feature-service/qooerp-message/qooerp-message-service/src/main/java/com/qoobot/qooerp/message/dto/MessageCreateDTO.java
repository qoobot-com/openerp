package com.qoobot.qooerp.message.dto;

import lombok.Data;

import java.util.List;

@Data
public class MessageCreateDTO {
    private String title;
    private String content;
    private Integer type;
    private Integer priority;
    private String channels;
    private List<Long> receiverIds;
}

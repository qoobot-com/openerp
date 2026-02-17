package com.qoobot.qooerp.message.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UnreadMessageDTO {
    private Long id;
    private String messageNo;
    private String title;
    private Integer type;
    private Integer priority;
    private String senderName;
    private LocalDateTime createTime;
}

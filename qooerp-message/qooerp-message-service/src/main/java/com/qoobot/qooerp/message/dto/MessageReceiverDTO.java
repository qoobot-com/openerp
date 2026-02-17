package com.qoobot.qooerp.message.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MessageReceiverDTO {
    private Long id;
    private Long messageId;
    private Long receiverId;
    private String receiverName;
    private Integer readStatus;
    private LocalDateTime readTime;
}

package com.qoobot.qooerp.message.dto;

import lombok.Data;

@Data
public class MessageReceiverQueryDTO {
    private Long current;
    private Long size;
    private Long messageId;
    private Long receiverId;
    private Integer readStatus;
}

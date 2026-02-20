package com.qoobot.qooerp.message.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class MessageDTO {
    private Long id;
    private String messageNo;
    private String title;
    private String content;
    private Integer type;
    private Integer priority;
    private Integer status;
    private Long senderId;
    private String senderName;
    private String channels;
    private LocalDateTime sendTime;
    private LocalDateTime createTime;
    private List<MessageReceiverDTO> receivers;
}

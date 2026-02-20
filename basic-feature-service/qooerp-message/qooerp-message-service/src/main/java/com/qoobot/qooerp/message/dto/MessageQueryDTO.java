package com.qoobot.qooerp.message.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MessageQueryDTO {
    private Long current;
    private Long size;
    private String messageNo;
    private Integer type;
    private Integer status;
    private Integer readStatus;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}

package com.qoobot.qooerp.message.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class MessageStatisticsDTO {
    private Long id;
    private LocalDate statDate;
    private String statType;
    private Integer messageType;
    private String channelCode;
    private Long totalCount;
    private LocalDateTime createTime;
}

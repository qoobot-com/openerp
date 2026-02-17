package com.qoobot.qooerp.message.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class MessageStatisticsQueryDTO {
    private Long current;
    private Long size;
    private LocalDate startDate;
    private LocalDate endDate;
    private String statType;
    private Integer messageType;
    private String channelCode;
}

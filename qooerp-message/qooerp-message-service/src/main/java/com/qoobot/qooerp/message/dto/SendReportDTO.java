package com.qoobot.qooerp.message.dto;

import lombok.Data;

@Data
public class SendReportDTO {
    private Long messageId;
    private String messageNo;
    private Integer totalReceivers;
    private Integer successCount;
    private Integer failureCount;
}

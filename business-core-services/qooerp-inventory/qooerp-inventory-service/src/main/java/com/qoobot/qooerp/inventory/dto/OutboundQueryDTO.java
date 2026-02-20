package com.qoobot.qooerp.inventory.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OutboundQueryDTO {
    private String outboundNo;
    private String outboundType;
    private Long warehouseId;
    private Long customerId;
    private String outboundStatus;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer current = 1;
    private Integer size = 10;
}

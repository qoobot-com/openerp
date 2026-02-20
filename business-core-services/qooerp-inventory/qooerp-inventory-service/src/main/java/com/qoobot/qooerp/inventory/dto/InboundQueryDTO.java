package com.qoobot.qooerp.inventory.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class InboundQueryDTO {
    private String inboundNo;
    private String inboundType;
    private Long warehouseId;
    private Long supplierId;
    private String inboundStatus;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer current = 1;
    private Integer size = 10;
}

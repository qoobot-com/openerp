package com.qoobot.qooerp.inventory.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TransferQueryDTO {
    private String transferNo;
    private String transferType;
    private Long sourceWarehouseId;
    private Long targetWarehouseId;
    private String transferStatus;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer current = 1;
    private Integer size = 10;
}

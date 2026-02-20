package com.qoobot.qooerp.inventory.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StockRecordQueryDTO {
    private String recordType;
    private String businessType;
    private String businessNo;
    private Long warehouseId;
    private Long materialId;
    private String materialCode;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer current = 1;
    private Integer size = 10;
}

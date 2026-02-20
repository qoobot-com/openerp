package com.qoobot.qooerp.inventory.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class StockRecordDTO {
    private Long id;
    private String recordType;
    private String businessType;
    private String businessNo;
    private Long warehouseId;
    private String warehouseName;
    private Long materialId;
    private String materialCode;
    private String materialName;
    private String materialSpec;
    private String materialUnit;
    private BigDecimal beforeQuantity;
    private BigDecimal changeQuantity;
    private BigDecimal afterQuantity;
    private String batchNo;
    private String operatorName;
    private LocalDateTime recordTime;
}

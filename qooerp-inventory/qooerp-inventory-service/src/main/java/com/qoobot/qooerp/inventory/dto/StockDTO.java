package com.qoobot.qooerp.inventory.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class StockDTO {
    private Long id;
    private Long warehouseId;
    private String warehouseName;
    private Long materialId;
    private String materialCode;
    private String materialName;
    private String materialSpec;
    private String materialUnit;
    private BigDecimal quantity;
    private BigDecimal availableQuantity;
    private BigDecimal lockedQuantity;
    private String stockStatus;
    private String batchNo;
}

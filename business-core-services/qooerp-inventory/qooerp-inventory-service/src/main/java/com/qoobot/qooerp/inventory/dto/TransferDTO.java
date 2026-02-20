package com.qoobot.qooerp.inventory.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class TransferDTO {
    private Long id;
    private String transferNo;
    private String transferType;
    private Long sourceWarehouseId;
    private String sourceWarehouseName;
    private Long targetWarehouseId;
    private String targetWarehouseName;
    private String transferStatus;
    private BigDecimal totalQuantity;
    private LocalDateTime transferDate;
    private String operatorName;
    private LocalDateTime auditTime;
    private List<TransferDetailDTO> details;
}

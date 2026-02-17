package com.qoobot.qooerp.inventory.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class InboundDTO {
    private Long id;
    private String inboundNo;
    private String inboundType;
    private String sourceType;
    private Long sourceId;
    private Long warehouseId;
    private String warehouseName;
    private Long supplierId;
    private String supplierName;
    private String inboundStatus;
    private BigDecimal totalQuantity;
    private BigDecimal totalAmount;
    private LocalDateTime inboundDate;
    private String operatorName;
    private LocalDateTime auditTime;
    private List<InboundDetailDTO> details;
}

package com.qoobot.qooerp.inventory.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class InboundDetailDTO {
    private Long id;
    private Long inboundId;
    private Long materialId;
    private String materialCode;
    private String materialName;
    private String materialSpec;
    private String materialUnit;
    private BigDecimal quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
    private String batchNo;
    private LocalDateTime productionDate;
    private LocalDateTime expiryDate;
    private BigDecimal actualQuantity;
}

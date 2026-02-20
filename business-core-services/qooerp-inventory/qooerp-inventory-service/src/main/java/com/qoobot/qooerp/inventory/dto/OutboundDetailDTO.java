package com.qoobot.qooerp.inventory.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OutboundDetailDTO {
    private Long id;
    private Long outboundId;
    private Long materialId;
    private String materialCode;
    private String materialName;
    private String materialSpec;
    private String materialUnit;
    private BigDecimal quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
    private String batchNo;
    private BigDecimal actualQuantity;
}

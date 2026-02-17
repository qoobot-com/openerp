package com.qoobot.qooerp.inventory.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferDetailDTO {
    private Long id;
    private Long transferId;
    private Long materialId;
    private String materialCode;
    private String materialName;
    private String materialSpec;
    private String materialUnit;
    private BigDecimal quantity;
    private BigDecimal actualQuantity;
}

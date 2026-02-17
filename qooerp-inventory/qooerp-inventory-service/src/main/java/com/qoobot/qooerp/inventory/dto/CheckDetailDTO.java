package com.qoobot.qooerp.inventory.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CheckDetailDTO {
    private Long id;
    private Long checkId;
    private Long materialId;
    private String materialCode;
    private String materialName;
    private String materialSpec;
    private String materialUnit;
    private BigDecimal systemQuantity;
    private BigDecimal actualQuantity;
    private BigDecimal diffQuantity;
    private String diffReason;
    private String batchNo;
}

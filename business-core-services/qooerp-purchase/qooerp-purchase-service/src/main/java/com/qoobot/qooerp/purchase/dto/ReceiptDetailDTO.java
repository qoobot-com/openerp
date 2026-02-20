package com.qoobot.qooerp.purchase.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ReceiptDetailDTO {

    private Long id;

    private Long materialId;

    private String materialCode;

    private String materialName;

    private String specification;

    private String unit;

    private BigDecimal quantity;

    private BigDecimal unitPrice;

    private BigDecimal amount;

    private String batchNo;

    private LocalDate productionDate;

    private LocalDate expiryDate;

    private String remark;
}

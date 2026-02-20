package com.qoobot.qooerp.purchase.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderDetailDTO {

    private Long id;

    private Long materialId;

    private String materialCode;

    private String materialName;

    private String specification;

    private String unit;

    private BigDecimal quantity;

    private BigDecimal unitPrice;

    private BigDecimal amount;

    private BigDecimal discountRate;

    private BigDecimal discountAmount;

    private BigDecimal payableAmount;

    private BigDecimal receivedQuantity;

    private String remark;
}

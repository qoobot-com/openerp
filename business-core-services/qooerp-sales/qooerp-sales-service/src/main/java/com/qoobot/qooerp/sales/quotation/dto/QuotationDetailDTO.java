package com.qoobot.qooerp.sales.quotation.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 报价明细DTO
 */
@Data
public class QuotationDetailDTO {

    private Long id;
    private Long quotationId;
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
    private String remark;
}

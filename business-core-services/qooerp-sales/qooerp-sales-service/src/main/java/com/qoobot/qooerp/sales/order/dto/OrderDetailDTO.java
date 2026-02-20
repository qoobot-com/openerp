package com.qoobot.qooerp.sales.order.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 订单明细DTO
 */
@Data
public class OrderDetailDTO {

    private Long id;
    private Long orderId;
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
    private BigDecimal shippedQuantity;
    private String remark;
}

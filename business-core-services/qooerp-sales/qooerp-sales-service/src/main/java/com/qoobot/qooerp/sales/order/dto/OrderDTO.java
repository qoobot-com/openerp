package com.qoobot.qooerp.sales.order.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 订单DTO
 */
@Data
public class OrderDTO {

    private Long id;
    private String orderCode;
    private Long customerId;
    private String customerName;
    private LocalDate orderDate;
    private LocalDate deliveryDate;
    private BigDecimal orderAmount;
    private BigDecimal discountAmount;
    private BigDecimal payableAmount;
    private String orderStatus;
    private String orderType;
    private Long salespersonId;
    private String salespersonName;
    private Long warehouseId;
    private String warehouseName;
    private String remark;
    private List<OrderDetailDTO> details;
}

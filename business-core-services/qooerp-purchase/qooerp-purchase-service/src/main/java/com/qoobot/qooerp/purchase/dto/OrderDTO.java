package com.qoobot.qooerp.purchase.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDTO {

    private Long id;

    private String orderCode;

    private Long supplierId;

    private String supplierName;

    private String orderDate;

    private String deliveryDate;

    private BigDecimal orderAmount;

    private BigDecimal discountAmount;

    private BigDecimal payableAmount;

    private String orderStatus;

    private String orderStatusName;

    private Long purchaserId;

    private String purchaserName;

    private Long warehouseId;

    private String warehouseName;

    private String remark;

    private LocalDateTime createTime;

    private List<OrderDetailDTO> details;
}

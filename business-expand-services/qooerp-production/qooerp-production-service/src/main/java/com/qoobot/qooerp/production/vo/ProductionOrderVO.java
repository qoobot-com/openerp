package com.qoobot.qooerp.production.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 生产订单VO
 */
@Data
public class ProductionOrderVO {

    private Long id;
    private String orderNo;
    private Long planId;
    private String planNo;
    private Long productId;
    private String productName;
    private String productCode;
    private BigDecimal orderQuantity;
    private BigDecimal completedQuantity;
    private BigDecimal qualifiedQuantity;
    private BigDecimal rejectQuantity;
    private BigDecimal progressRate;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long workshopId;
    private String workshopName;
    private String status;
    private String statusDesc;
    private String remark;
    private Long tenantId;
    private String createdBy;
    private LocalDateTime createdTime;
    private String updatedBy;
    private LocalDateTime updatedTime;
}

package com.qoobot.qooerp.production.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 生产报工VO
 */
@Data
public class ProductionReportVO {

    private Long id;
    private String reportNo;
    private Long orderId;
    private String orderNo;
    private Long processId;
    private String processName;
    private Long productId;
    private String productName;
    private BigDecimal reportQuantity;
    private BigDecimal qualifiedQuantity;
    private BigDecimal rejectQuantity;
    private BigDecimal qualifiedRate;
    private BigDecimal workTime;
    private Long workerId;
    private String workerName;
    private Long equipmentId;
    private String equipmentName;
    private LocalDate reportDate;
    private String status;
    private String statusDesc;
    private String remark;
    private Long tenantId;
    private String createdBy;
    private LocalDateTime createdTime;
    private String updatedBy;
    private LocalDateTime updatedTime;
}

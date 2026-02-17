package com.qoobot.qooerp.production.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ProductionQualityVO {
    private Long id;
    private String qualityNo;
    private Long orderId;
    private String orderNo;
    private Long reportId;
    private String reportNo;
    private Long productId;
    private String productName;
    private String productCode;
    private BigDecimal inspectionQuantity;
    private BigDecimal qualifiedQuantity;
    private BigDecimal rejectQuantity;
    private BigDecimal qualifiedRate;
    private Long inspectorId;
    private String inspectorName;
    private LocalDate inspectionDate;
    private String status;
    private String statusDesc;
    private String remark;
    private Long tenantId;
    private String createdBy;
    private LocalDateTime createdTime;
    private String updatedBy;
    private LocalDateTime updatedTime;
}

package com.qoobot.qooerp.production.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ProductionReceiptVO {
    private Long id;
    private String receiptNo;
    private Long orderId;
    private String orderNo;
    private Long productId;
    private String productName;
    private String productCode;
    private BigDecimal receiptQuantity;
    private BigDecimal qualifiedQuantity;
    private BigDecimal rejectQuantity;
    private BigDecimal qualifiedRate;
    private Long warehouseId;
    private String warehouseName;
    private LocalDate receiptDate;
    private Long qualityCheckId;
    private String status;
    private String statusDesc;
    private String remark;
    private Long tenantId;
    private String createdBy;
    private LocalDateTime createdTime;
    private String updatedBy;
    private LocalDateTime updatedTime;
}

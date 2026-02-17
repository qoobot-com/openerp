package com.qoobot.qooerp.production.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ProductionScrapVO {
    private Long id;
    private String scrapNo;
    private Long orderId;
    private String orderNo;
    private Long reportId;
    private Long productId;
    private String productName;
    private String productCode;
    private BigDecimal scrapQuantity;
    private String scrapReason;
    private String scrapType;
    private Long processId;
    private String processName;
    private LocalDate scrapDate;
    private String status;
    private String statusDesc;
    private String remark;
    private Long tenantId;
    private String createdBy;
    private LocalDateTime createdTime;
    private String updatedBy;
    private LocalDateTime updatedTime;
}

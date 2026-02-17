package com.qoobot.qooerp.production.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ProductionReworkVO {
    private Long id;
    private String reworkNo;
    private Long orderId;
    private String orderNo;
    private Long qualityId;
    private Long productId;
    private String productName;
    private String productCode;
    private BigDecimal reworkQuantity;
    private String reworkReason;
    private Long processId;
    private String processName;
    private LocalDate reworkDate;
    private String status;
    private String statusDesc;
    private String remark;
    private Long tenantId;
    private String createdBy;
    private LocalDateTime createdTime;
    private String updatedBy;
    private LocalDateTime updatedTime;
}

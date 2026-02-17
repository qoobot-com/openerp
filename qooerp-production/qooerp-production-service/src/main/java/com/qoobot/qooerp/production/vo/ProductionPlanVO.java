package com.qoobot.qooerp.production.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 生产计划VO
 */
@Data
public class ProductionPlanVO {

    private Long id;
    private String planNo;
    private String planName;
    private Long productId;
    private String productName;
    private String productCode;
    private BigDecimal planQuantity;
    private BigDecimal completedQuantity;
    private BigDecimal progressRate;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer priority;
    private String status;
    private String statusDesc;
    private String mrpStatus;
    private String remark;
    private Long tenantId;
    private String createdBy;
    private LocalDateTime createdTime;
    private String updatedBy;
    private LocalDateTime updatedTime;
}

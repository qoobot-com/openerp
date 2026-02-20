package com.qoobot.qooerp.production.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 生产质检实体
 */
@Data
@TableName("production_quality")
public class ProductionQuality {

    @TableId(value = "id", type = IdType.AUTO)
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

    private String remark;

    private Long tenantId;

    private String createdBy;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    private String updatedBy;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;

    @TableLogic
    private Integer deleted;
}

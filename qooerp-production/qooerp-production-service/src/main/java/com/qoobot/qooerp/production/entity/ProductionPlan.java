package com.qoobot.qooerp.production.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 生产计划实体
 */
@Data
@TableName("production_plan")
public class ProductionPlan {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String planNo;

    private String planName;

    private Long productId;

    private String productName;

    private String productCode;

    private BigDecimal planQuantity;

    private BigDecimal completedQuantity;

    private LocalDate startDate;

    private LocalDate endDate;

    private Integer priority;

    private String status;

    private String mrpStatus;

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

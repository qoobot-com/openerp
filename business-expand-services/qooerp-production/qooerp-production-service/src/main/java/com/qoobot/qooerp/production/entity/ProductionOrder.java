package com.qoobot.qooerp.production.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 生产订单实体
 */
@Data
@TableName("production_order")
public class ProductionOrder {

    @TableId(value = "id", type = IdType.AUTO)
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

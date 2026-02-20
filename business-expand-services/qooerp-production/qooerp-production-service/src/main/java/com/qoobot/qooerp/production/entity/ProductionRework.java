package com.qoobot.qooerp.production.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 生产返工实体
 */
@Data
@TableName("production_rework")
public class ProductionRework {

    @TableId(value = "id", type = IdType.AUTO)
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

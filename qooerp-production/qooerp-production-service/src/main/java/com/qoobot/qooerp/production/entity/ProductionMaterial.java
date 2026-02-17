package com.qoobot.qooerp.production.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 生产领料实体
 */
@Data
@TableName("production_material")
public class ProductionMaterial {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String materialNo;

    private Long orderId;

    private String orderNo;

    private Long materialId;

    private String materialName;

    private String materialCode;

    private BigDecimal requiredQuantity;

    private BigDecimal issuedQuantity;

    private Long warehouseId;

    private String warehouseName;

    private LocalDate issueDate;

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

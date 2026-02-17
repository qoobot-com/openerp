package com.qoobot.qooerp.production.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 生产入库实体
 */
@Data
@TableName("production_receipt")
public class ProductionReceipt {

    @TableId(value = "id", type = IdType.AUTO)
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

    private Long warehouseId;

    private String warehouseName;

    private LocalDate receiptDate;

    private Long qualityCheckId;

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

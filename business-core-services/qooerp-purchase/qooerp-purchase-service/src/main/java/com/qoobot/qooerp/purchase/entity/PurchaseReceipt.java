package com.qoobot.qooerp.purchase.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("purchase_receipt")
public class PurchaseReceipt {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    @TableField("tenant_id")
    private Long tenantId;

    @TableField("receipt_code")
    private String receiptCode;

    @TableField("order_id")
    private Long orderId;

    @TableField("order_code")
    private String orderCode;

    @TableField("supplier_id")
    private Long supplierId;

    @TableField("supplier_name")
    private String supplierName;

    @TableField("receipt_date")
    private LocalDate receiptDate;

    @TableField("receipt_amount")
    private BigDecimal receiptAmount;

    @TableField("receipt_status")
    private String receiptStatus;

    @TableField("warehouse_id")
    private Long warehouseId;

    @TableField("warehouse_name")
    private String warehouseName;

    @TableField("operator_id")
    private Long operatorId;

    @TableField("operator_name")
    private String operatorName;

    @TableField("remark")
    private String remark;

    @TableField("created_by")
    private Long createdBy;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField("updated_by")
    private Long updatedBy;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    @TableField("deleted")
    private Integer deleted;

    @Version
    @TableField("version")
    private Integer version;
}

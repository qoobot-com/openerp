package com.qoobot.qooerp.purchase.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("purchase_order")
public class PurchaseOrder {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    @TableField("tenant_id")
    private Long tenantId;

    @TableField("order_code")
    private String orderCode;

    @TableField("supplier_id")
    private Long supplierId;

    @TableField("supplier_name")
    private String supplierName;

    @TableField("order_date")
    private LocalDate orderDate;

    @TableField("delivery_date")
    private LocalDate deliveryDate;

    @TableField("order_amount")
    private BigDecimal orderAmount;

    @TableField("discount_amount")
    private BigDecimal discountAmount;

    @TableField("payable_amount")
    private BigDecimal payableAmount;

    @TableField("order_status")
    private String orderStatus;

    @TableField("purchaser_id")
    private Long purchaserId;

    @TableField("purchaser_name")
    private String purchaserName;

    @TableField("warehouse_id")
    private Long warehouseId;

    @TableField("warehouse_name")
    private String warehouseName;

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

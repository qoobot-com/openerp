package com.qoobot.qooerp.purchase.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("purchase_receipt_detail")
public class PurchaseReceiptDetail {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    @TableField("tenant_id")
    private Long tenantId;

    @TableField("receipt_id")
    private Long receiptId;

    @TableField("order_detail_id")
    private Long orderDetailId;

    @TableField("material_id")
    private Long materialId;

    @TableField("material_code")
    private String materialCode;

    @TableField("material_name")
    private String materialName;

    @TableField("specification")
    private String specification;

    @TableField("unit")
    private String unit;

    @TableField("quantity")
    private BigDecimal quantity;

    @TableField("unit_price")
    private BigDecimal unitPrice;

    @TableField("amount")
    private BigDecimal amount;

    @TableField("batch_no")
    private String batchNo;

    @TableField("production_date")
    private LocalDate productionDate;

    @TableField("expiry_date")
    private LocalDate expiryDate;

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
}

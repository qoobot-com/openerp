package com.qoobot.qooerp.inventory.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("inventory_stock_record")
public class InventoryStockRecord {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @TableField("tenant_id")
    private Long tenantId;

    @TableField("record_type")
    private String recordType;

    @TableField("business_type")
    private String businessType;

    @TableField("business_no")
    private String businessNo;

    @TableField("warehouse_id")
    private Long warehouseId;

    @TableField("warehouse_name")
    private String warehouseName;

    @TableField("material_id")
    private Long materialId;

    @TableField("material_code")
    private String materialCode;

    @TableField("material_name")
    private String materialName;

    @TableField("material_spec")
    private String materialSpec;

    @TableField("material_unit")
    private String materialUnit;

    @TableField("before_quantity")
    private BigDecimal beforeQuantity;

    @TableField("change_quantity")
    private BigDecimal changeQuantity;

    @TableField("after_quantity")
    private BigDecimal afterQuantity;

    @TableField("batch_no")
    private String batchNo;

    @TableField("operator_id")
    private Long operatorId;

    @TableField("operator_name")
    private String operatorName;

    @TableField("record_time")
    private LocalDateTime recordTime;

    @TableField("deleted")
    @TableLogic
    private Boolean deleted;

    @TableField("remark")
    private String remark;
}

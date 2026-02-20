package com.qoobot.qooerp.inventory.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("inventory_stock")
public class InventoryStock {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @TableField("tenant_id")
    private Long tenantId;

    @TableField("warehouse_id")
    private Long warehouseId;

    @TableField("material_id")
    private Long materialId;

    @TableField("quantity")
    private BigDecimal quantity;

    @TableField("available_quantity")
    private BigDecimal availableQuantity;

    @TableField("locked_quantity")
    private BigDecimal lockedQuantity;

    @TableField("in_transit_quantity")
    private BigDecimal inTransitQuantity;

    @TableField("stock_status")
    private String stockStatus;

    @TableField("batch_no")
    private String batchNo;

    @TableField("production_date")
    private LocalDateTime productionDate;

    @TableField("expiry_date")
    private LocalDateTime expiryDate;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;

    @TableField("deleted")
    @TableLogic
    private Boolean deleted;

    @TableField("remark")
    private String remark;
}

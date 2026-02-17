package com.qoobot.qooerp.inventory.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("inventory_stock_inbound_detail")
public class InventoryStockInboundDetail {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @TableField("tenant_id")
    private Long tenantId;

    @TableField("inbound_id")
    private Long inboundId;

    @TableField("inbound_no")
    private String inboundNo;

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

    @TableField("quantity")
    private BigDecimal quantity;

    @TableField("unit_price")
    private BigDecimal unitPrice;

    @TableField("total_price")
    private BigDecimal totalPrice;

    @TableField("batch_no")
    private String batchNo;

    @TableField("production_date")
    private LocalDateTime productionDate;

    @TableField("expiry_date")
    private LocalDateTime expiryDate;

    @TableField("actual_quantity")
    private BigDecimal actualQuantity;

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

package com.qoobot.qooerp.inventory.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("inventory_material")
public class InventoryMaterial {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @TableField("tenant_id")
    private Long tenantId;

    @TableField("material_code")
    private String materialCode;

    @TableField("material_name")
    private String materialName;

    @TableField("material_spec")
    private String materialSpec;

    @TableField("material_unit")
    private String materialUnit;

    @TableField("category_id")
    private Long categoryId;

    @TableField("safety_stock")
    private BigDecimal safetyStock;

    @TableField("max_stock")
    private BigDecimal maxStock;

    @TableField("material_status")
    private String materialStatus;

    @TableField("barcode")
    private String barcode;

    @TableField("description")
    private String description;

    @TableField("sort_order")
    private Integer sortOrder;

    @TableField("create_by")
    private Long createBy;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_by")
    private Long updateBy;

    @TableField("update_time")
    private LocalDateTime updateTime;

    @TableField("deleted")
    @TableLogic
    private Boolean deleted;

    @TableField("remark")
    private String remark;
}

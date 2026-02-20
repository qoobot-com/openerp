package com.qoobot.qooerp.inventory.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("inventory_material_category")
public class InventoryMaterialCategory {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @TableField("tenant_id")
    private Long tenantId;

    @TableField("category_code")
    private String categoryCode;

    @TableField("category_name")
    private String categoryName;

    @TableField("parent_id")
    private Long parentId;

    @TableField("level")
    private Integer level;

    @TableField("path")
    private String path;

    @TableField("sort_order")
    private Integer sortOrder;

    @TableField("category_status")
    private String categoryStatus;

    @TableField("description")
    private String description;

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

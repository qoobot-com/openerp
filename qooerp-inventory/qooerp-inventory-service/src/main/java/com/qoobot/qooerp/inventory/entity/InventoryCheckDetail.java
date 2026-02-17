package com.qoobot.qooerp.inventory.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("inventory_check_detail")
public class InventoryCheckDetail {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @TableField("tenant_id")
    private Long tenantId;

    @TableField("check_id")
    private Long checkId;

    @TableField("check_no")
    private String checkNo;

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

    @TableField("system_quantity")
    private BigDecimal systemQuantity;

    @TableField("actual_quantity")
    private BigDecimal actualQuantity;

    @TableField("diff_quantity")
    private BigDecimal diffQuantity;

    @TableField("diff_reason")
    private String diffReason;

    @TableField("batch_no")
    private String batchNo;

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

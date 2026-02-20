package com.qoobot.qooerp.inventory.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("inventory_check")
public class InventoryCheck {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @TableField("tenant_id")
    private Long tenantId;

    @TableField("check_no")
    private String checkNo;

    @TableField("check_type")
    private String checkType;

    @TableField("warehouse_id")
    private Long warehouseId;

    @TableField("warehouse_name")
    private String warehouseName;

    @TableField("check_status")
    private String checkStatus;

    @TableField("check_date")
    private LocalDateTime checkDate;

    @TableField("total_items")
    private Integer totalItems;

    @TableField("diff_items")
    private Integer diffItems;

    @TableField("operator_id")
    private Long operatorId;

    @TableField("operator_name")
    private String operatorName;

    @TableField("auditor_id")
    private Long auditorId;

    @TableField("auditor_name")
    private String auditorName;

    @TableField("audit_time")
    private LocalDateTime auditTime;

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

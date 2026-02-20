package com.qoobot.qooerp.inventory.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("inventory_transfer")
public class InventoryTransfer {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @TableField("tenant_id")
    private Long tenantId;

    @TableField("transfer_no")
    private String transferNo;

    @TableField("transfer_type")
    private String transferType;

    @TableField("source_warehouse_id")
    private Long sourceWarehouseId;

    @TableField("source_warehouse_name")
    private String sourceWarehouseName;

    @TableField("target_warehouse_id")
    private Long targetWarehouseId;

    @TableField("target_warehouse_name")
    private String targetWarehouseName;

    @TableField("transfer_status")
    private String transferStatus;

    @TableField("total_quantity")
    private BigDecimal totalQuantity;

    @TableField("transfer_date")
    private LocalDateTime transferDate;

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

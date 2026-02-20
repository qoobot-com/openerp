package com.qoobot.qooerp.inventory.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("inventory_stock_inbound")
public class InventoryStockInbound {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @TableField("tenant_id")
    private Long tenantId;

    @TableField("inbound_no")
    private String inboundNo;

    @TableField("inbound_type")
    private String inboundType;

    @TableField("source_type")
    private String sourceType;

    @TableField("source_id")
    private Long sourceId;

    @TableField("warehouse_id")
    private Long warehouseId;

    @TableField("supplier_id")
    private Long supplierId;

    @TableField("inbound_status")
    private String inboundStatus;

    @TableField("total_quantity")
    private BigDecimal totalQuantity;

    @TableField("total_amount")
    private BigDecimal totalAmount;

    @TableField("inbound_date")
    private LocalDateTime inboundDate;

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

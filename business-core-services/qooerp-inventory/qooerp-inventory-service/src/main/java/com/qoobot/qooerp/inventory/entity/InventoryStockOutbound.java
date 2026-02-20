package com.qoobot.qooerp.inventory.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("inventory_stock_outbound")
public class InventoryStockOutbound {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @TableField("tenant_id")
    private Long tenantId;

    @TableField("outbound_no")
    private String outboundNo;

    @TableField("outbound_type")
    private String outboundType;

    @TableField("target_type")
    private String targetType;

    @TableField("target_id")
    private Long targetId;

    @TableField("warehouse_id")
    private Long warehouseId;

    @TableField("customer_id")
    private Long customerId;

    @TableField("outbound_status")
    private String outboundStatus;

    @TableField("total_quantity")
    private BigDecimal totalQuantity;

    @TableField("total_amount")
    private BigDecimal totalAmount;

    @TableField("outbound_date")
    private LocalDateTime outboundDate;

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

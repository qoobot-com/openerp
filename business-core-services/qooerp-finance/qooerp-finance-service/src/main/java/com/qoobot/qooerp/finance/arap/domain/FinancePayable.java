package com.qoobot.qooerp.finance.arap.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.qoobot.qooerp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 应付账款实体
 *
 * @author QooERP Team
 * @since 20xx-xx-xx
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("finance_payable")
public class FinancePayable extends BaseEntity {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 租户ID
     */
    @TableField("tenant_id")
    private Long tenantId;

    /**
     * 应付单号
     */
    @TableField("payable_code")
    private String payableCode;

    /**
     * 供应商ID
     */
    @TableField("supplier_id")
    private Long supplierId;

    /**
     * 供应商名称
     */
    @TableField("supplier_name")
    private String supplierName;

    /**
     * 业务类型
     */
    @TableField("business_type")
    private String businessType;

    /**
     * 关联单据ID
     */
    @TableField("related_order_id")
    private Long relatedOrderId;

    /**
     * 关联单据号
     */
    @TableField("related_order_code")
    private String relatedOrderCode;

    /**
     * 应付金额
     */
    @TableField("payable_amount")
    private BigDecimal payableAmount;

    /**
     * 已付金额
     */
    @TableField("paid_amount")
    private BigDecimal paidAmount;

    /**
     * 未付金额
     */
    @TableField("unpaid_amount")
    private BigDecimal unpaidAmount;

    /**
     * 应付日期
     */
    @TableField("payable_date")
    private LocalDate payableDate;

    /**
     * 到期日期
     */
    @TableField("due_date")
    private LocalDate dueDate;

    /**
     * 账龄（天）
     */
    @TableField("aging_days")
    private Integer agingDays;

    /**
     * 单据状态：UNPAID-未付，PARTIAL-部分付款，PAID-已付，CANCELLED-已取消
     */
    @TableField("status")
    private String status;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

    /**
     * 删除标记：0-未删除，1-已删除
     */
    @TableLogic
    @TableField("deleted")
    private Integer deleted;

    /**
     * 版本号
     */
    @Version
    @TableField("version")
    private Integer version;
}

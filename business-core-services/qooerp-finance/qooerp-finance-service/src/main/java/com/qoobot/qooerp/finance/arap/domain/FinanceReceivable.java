package com.qoobot.qooerp.finance.arap.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.qoobot.qooerp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 应收账款实体
 *
 * @author QooERP Team
 * @since 20xx-xx-xx
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("finance_receivable")
public class FinanceReceivable extends BaseEntity {

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
     * 应收单号
     */
    @TableField("receivable_code")
    private String receivableCode;

    /**
     * 客户ID
     */
    @TableField("customer_id")
    private Long customerId;

    /**
     * 客户名称
     */
    @TableField("customer_name")
    private String customerName;

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
     * 应收金额
     */
    @TableField("receivable_amount")
    private BigDecimal receivableAmount;

    /**
     * 已收金额
     */
    @TableField("received_amount")
    private BigDecimal receivedAmount;

    /**
     * 未收金额
     */
    @TableField("unpaid_amount")
    private BigDecimal unpaidAmount;

    /**
     * 应收日期
     */
    @TableField("receivable_date")
    private LocalDate receivableDate;

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
     * 单据状态：UNPAID-未收，PARTIAL-部分收款，PAID-已收，CANCELLED-已取消
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

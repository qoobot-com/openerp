package com.qoobot.qooerp.finance.arap.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.qoobot.qooerp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 收款记录实体
 *
 * @author QooERP Team
 * @since 2026-02-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("finance_receipt")
public class FinanceReceipt extends BaseEntity {

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
     * 收款单号
     */
    @TableField("receipt_code")
    private String receiptCode;

    /**
     * 应收单据ID
     */
    @TableField("receivable_id")
    private Long receivableId;

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
     * 收款金额
     */
    @TableField("receipt_amount")
    private BigDecimal receiptAmount;

    /**
     * 收款日期
     */
    @TableField("receipt_date")
    private LocalDate receiptDate;

    /**
     * 收款方式：CASH-现金，BANK_TRANSFER-银行转账，CHECK-支票，ALIPAY-支付宝，WECHAT-微信
     */
    @TableField("payment_method")
    private String paymentMethod;

    /**
     * 银行账户
     */
    @TableField("bank_account")
    private String bankAccount;

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
}

package com.qoobot.qooerp.finance.arap.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.qoobot.qooerp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 付款记录实体
 *
 * @author QooERP Team
 * @since 20xx-xx-xx
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("finance_payment")
public class FinancePayment extends BaseEntity {

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
     * 付款单号
     */
    @TableField("payment_code")
    private String paymentCode;

    /**
     * 应付单据ID
     */
    @TableField("payable_id")
    private Long payableId;

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
     * 付款金额
     */
    @TableField("payment_amount")
    private BigDecimal paymentAmount;

    /**
     * 付款日期
     */
    @TableField("payment_date")
    private LocalDate paymentDate;

    /**
     * 付款方式：CASH-现金，BANK_TRANSFER-银行转账，CHECK-支票，ALIPAY-支付宝，WECHAT-微信
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

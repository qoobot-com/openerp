package com.qoobot.qooerp.sales.receipt.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qoobot.qooerp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 收款单实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sales_receipt")
public class SalesReceipt extends BaseEntity {

    /** 收款单编号 */
    @TableField("receipt_no")
    private String receiptNo;

    /** 关联订单ID */
    @TableField("order_id")
    private Long orderId;

    /** 客户ID */
    @TableField("customer_id")
    private Long customerId;

    /** 客户名称 */
    @TableField("customer_name")
    private String customerName;

    /** 收款日期 */
    @TableField("receipt_date")
    private LocalDate receiptDate;

    /** 收款金额 */
    @TableField("receipt_amount")
    private BigDecimal receiptAmount;

    /** 收款方式 */
    @TableField("payment_method")
    private String paymentMethod;

    /** 收款账户 */
    @TableField("payment_account")
    private String paymentAccount;

    /** 收款状态 */
    @TableField("status")
    private String status;

    /** 收款人ID */
    @TableField("receiver_id")
    private Long receiverId;

    /** 核销订单ID */
    @TableField("writeoff_order_id")
    private Long writeoffOrderId;

    /** 核销金额 */
    @TableField("writeoff_amount")
    private BigDecimal writeoffAmount;

    /** 备注 */
    @TableField("remark")
    private String remark;
}

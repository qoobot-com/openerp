package com.qoobot.qooerp.sales.quotation.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qoobot.qooerp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 销售报价实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sales_quotation")
public class SalesQuotation extends BaseEntity {

    /** 报价单编号 */
    @TableField("quotation_code")
    private String quotationCode;

    /** 客户ID */
    @TableField("customer_id")
    private Long customerId;

    /** 客户名称 */
    @TableField("customer_name")
    private String customerName;

    /** 报价日期 */
    @TableField("quotation_date")
    private LocalDate quotationDate;

    /** 有效期至 */
    @TableField("valid_until")
    private LocalDate validUntil;

    /** 报价金额 */
    @TableField("quotation_amount")
    private BigDecimal quotationAmount;

    /** 折扣金额 */
    @TableField("discount_amount")
    private BigDecimal discountAmount;

    /** 实付金额 */
    @TableField("payable_amount")
    private BigDecimal payableAmount;

    /** 报价状态 */
    @TableField("quotation_status")
    private String quotationStatus;

    /** 销售员ID */
    @TableField("salesperson_id")
    private Long salespersonId;

    /** 销售员姓名 */
    @TableField("salesperson_name")
    private String salespersonName;

    /** 备注 */
    @TableField("remark")
    private String remark;
}

package com.qoobot.qooerp.finance.invoice.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 发票实体
 * 
 * @author QooERP Team
 * @since 20xx-xx-xx
 */
@Data
@TableName("finance_invoice")
public class FinanceInvoice {

    /**
     * 发票ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 发票编号
     */
    private String invoiceNo;

    /**
     * 发票类型（SALES-销项 PURCHASE-进项）
     */
    private String invoiceType;

    /**
     * 发票种类（PLAIN-普票 SPECIAL-专票 ELECTRONIC-电子发票）
     */
    private String invoiceCategory;

    /**
     * 客户ID（销项发票）
     */
    private Long customerId;

    /**
     * 供应商ID（进项发票）
     */
    private Long supplierId;

    /**
     * 开票日期
     */
    private LocalDate invoiceDate;

    /**
     * 发票金额（不含税）
     */
    private BigDecimal amount;

    /**
     * 税额
     */
    private BigDecimal taxAmount;

    /**
     * 价税合计
     */
    private BigDecimal totalAmount;

    /**
     * 税率
     */
    private BigDecimal taxRate;

    /**
     * 购买方名称
     */
    private String buyerName;

    /**
     * 购买方税号
     */
    private String buyerTaxNo;

    /**
     * 销售方名称
     */
    private String sellerName;

    /**
     * 销售方税号
     */
    private String sellerTaxNo;

    /**
     * 关联凭证ID
     */
    private Long voucherId;

    /**
     * 关联应收账款ID
     */
    private Long receivableId;

    /**
     * 关联应付账款ID
     */
    private Long payableId;

    /**
     * 发票状态（DRAFT-草稿 PENDING-待认证 VERIFIED-已认证 INVALID-已作废）
     */
    private String status;

    /**
     * 认证日期
     */
    private LocalDate verifyDate;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 创建人ID
     */
    private Long createBy;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 更新人ID
     */
    private Long updateBy;

    /**
     * 租户ID
     */
    private Long tenantId;
}

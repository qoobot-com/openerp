package com.qoobot.qooerp.finance.voucher.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.qoobot.qooerp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 会计凭证实体
 *
 * @author QooERP Team
 * @since 2026-02-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("finance_voucher")
public class FinanceVoucher extends BaseEntity {

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
     * 凭证编号
     */
    @TableField("voucher_code")
    private String voucherCode;

    /**
     * 凭证日期
     */
    @TableField("voucher_date")
    private LocalDate voucherDate;

    /**
     * 凭证类型：RECEIPT-收款凭证，PAYMENT-付款凭证，TRANSFER-转账凭证，ADJUSTMENT-调整凭证
     */
    @TableField("voucher_type")
    private String voucherType;

    /**
     * 会计期间：YYYY-MM
     */
    @TableField("accounting_period")
    private String accountingPeriod;

    /**
     * 借方金额
     */
    @TableField("debit_amount")
    private BigDecimal debitAmount;

    /**
     * 贷方金额
     */
    @TableField("credit_amount")
    private BigDecimal creditAmount;

    /**
     * 凭证摘要
     */
    @TableField("summary")
    private String summary;

    /**
     * 制单人ID
     */
    @TableField("creator_id")
    private Long creatorId;

    /**
     * 审核人ID
     */
    @TableField("reviewer_id")
    private Long reviewerId;

    /**
     * 审核状态：DRAFT-草稿，PENDING-待审核，APPROVED-已审核，REJECTED-已驳回
     */
    @TableField("review_status")
    private String reviewStatus;

    /**
     * 审核时间
     */
    @TableField("review_time")
    private LocalDateTime reviewTime;

    /**
     * 记账状态：UNPOSTED-未记账，POSTED-已记账
     */
    @TableField("posting_status")
    private String postingStatus;

    /**
     * 记账时间
     */
    @TableField("posting_time")
    private LocalDateTime postingTime;

    /**
     * 附件数
     */
    @TableField("attachment_count")
    private Integer attachmentCount;

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

    /**
     * 凭证明细列表
     */
    @TableField(exist = false)
    private List<FinanceVoucherDetail> details;
}

package com.qoobot.qooerp.finance.voucher.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 财务凭证DTO
 *
 * @author QooERP Team
 * @since 2026-02-17
 */
@Data
@Schema(description = "财务凭证DTO")
public class FinanceVoucherDTO {

    @Schema(description = "凭证ID")
    private Long id;

    @Schema(description = "凭证编号")
    private String voucherCode;

    @Schema(description = "会计期间ID")
    private Long periodId;

    @Schema(description = "凭证日期")
    private LocalDate voucherDate;

    @Schema(description = "凭证类型（RECEIPT-收款 PAYMENT-付款 TRANSFER-转账 OTHER-其他）")
    private String voucherType;

    @Schema(description = "摘要")
    private String summary;

    @Schema(description = "借方金额合计")
    private BigDecimal debitAmount;

    @Schema(description = "贷方金额合计")
    private BigDecimal creditAmount;

    @Schema(description = "制单人ID")
    private Long makerId;

    @Schema(description = "制单人姓名")
    private String makerName;

    @Schema(description = "审核状态（DRAFT-草稿 PENDING-待审核 APPROVED-已审核 REJECTED-已驳回）")
    private String auditStatus;

    @Schema(description = "审核人ID")
    private Long auditorId;

    @Schema(description = "审核人姓名")
    private String auditorName;

    @Schema(description = "审核时间")
    private String auditTime;

    @Schema(description = "记账状态（UNPOSTED-未记账 POSTED-已记账）")
    private String postingStatus;

    @Schema(description = "记账人ID")
    private Long posterId;

    @Schema(description = "记账人姓名")
    private String posterName;

    @Schema(description = "记账时间")
    private String postingTime;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "凭证明细列表")
    private List<FinanceVoucherDetailDTO> details;
}

package com.qoobot.qooerp.finance.invoice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 发票DTO
 *
 * @author QooERP Team
 * @since 2026-02-17
 */
@Data
@Schema(description = "发票DTO")
public class FinanceInvoiceDTO {

    @Schema(description = "发票ID")
    private Long id;

    @Schema(description = "发票编号")
    private String invoiceNo;

    @Schema(description = "发票类型（SALES-销项 PURCHASE-进项）")
    private String invoiceType;

    @Schema(description = "发票种类（PLAIN-普票 SPECIAL-专票 ELECTRONIC-电子发票）")
    private String invoiceCategory;

    @Schema(description = "客户ID（销项发票）")
    private Long customerId;

    @Schema(description = "供应商ID（进项发票）")
    private Long supplierId;

    @Schema(description = "开票日期")
    private LocalDate invoiceDate;

    @Schema(description = "发票金额（不含税）")
    private BigDecimal amount;

    @Schema(description = "税额")
    private BigDecimal taxAmount;

    @Schema(description = "价税合计")
    private BigDecimal totalAmount;

    @Schema(description = "税率")
    private BigDecimal taxRate;

    @Schema(description = "购买方名称")
    private String buyerName;

    @Schema(description = "购买方税号")
    private String buyerTaxNo;

    @Schema(description = "销售方名称")
    private String sellerName;

    @Schema(description = "销售方税号")
    private String sellerTaxNo;

    @Schema(description = "关联凭证ID")
    private Long voucherId;

    @Schema(description = "关联应收账款ID")
    private Long receivableId;

    @Schema(description = "关联应付账款ID")
    private Long payableId;

    @Schema(description = "发票状态（DRAFT-草稿 PENDING-待认证 VERIFIED-已认证 INVALID-已作废）")
    private String status;

    @Schema(description = "认证日期")
    private LocalDate verifyDate;

    @Schema(description = "备注")
    private String remark;
}

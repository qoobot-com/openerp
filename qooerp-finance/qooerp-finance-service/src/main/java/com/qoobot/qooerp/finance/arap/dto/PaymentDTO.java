package com.qoobot.qooerp.finance.arap.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 付款记录DTO
 *
 * @author QooERP Team
 * @since 2026-02-17
 */
@Data
@Schema(description = "付款记录DTO")
public class PaymentDTO {

    @Schema(description = "付款记录ID")
    private Long id;

    @Schema(description = "付款单号")
    private String paymentCode;

    @Schema(description = "应付单ID")
    private Long payableId;

    @Schema(description = "应付单号")
    private String payableCode;

    @Schema(description = "供应商ID")
    private Long supplierId;

    @Schema(description = "供应商名称")
    private String supplierName;

    @Schema(description = "付款金额")
    private BigDecimal paymentAmount;

    @Schema(description = "付款方式")
    private String paymentMethod;

    @Schema(description = "银行账户")
    private String bankAccount;

    @Schema(description = "付款日期")
    private LocalDate paymentDate;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建人")
    private String createdBy;

    @Schema(description = "创建时间")
    private String createdTime;
}

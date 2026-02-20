package com.qoobot.qooerp.finance.arap.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 应付账款DTO
 *
 * @author QooERP Team
 * @since 2026-02-17
 */
@Data
@Schema(description = "应付账款DTO")
public class PayableDTO {

    @Schema(description = "应付单ID")
    private Long id;

    @Schema(description = "应付单号")
    private String payableCode;

    @Schema(description = "供应商ID")
    private Long supplierId;

    @Schema(description = "供应商名称")
    private String supplierName;

    @Schema(description = "业务类型")
    private String businessType;

    @Schema(description = "关联单据ID")
    private Long relatedOrderId;

    @Schema(description = "关联单据号")
    private String relatedOrderCode;

    @Schema(description = "应付金额")
    private BigDecimal payableAmount;

    @Schema(description = "已付金额")
    private BigDecimal paidAmount;

    @Schema(description = "未付金额")
    private BigDecimal unpaidAmount;

    @Schema(description = "应付日期")
    private LocalDate payableDate;

    @Schema(description = "到期日期")
    private LocalDate dueDate;

    @Schema(description = "账龄（天）")
    private Integer agingDays;

    @Schema(description = "单据状态（UNPAID-未付 PARTIAL-部分付款 PAID-已付 CANCELLED-已取消）")
    private String status;

    @Schema(description = "备注")
    private String remark;
}

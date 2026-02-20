package com.qoobot.qooerp.finance.tax.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 企业所得税报表DTO
 *
 * @author QooERP Team
 * @since 2026-02-17
 */
@Data
@Schema(description = "企业所得税报表DTO")
public class CITReportDTO {

    @Schema(description = "CIT记录ID")
    private Long id;

    @Schema(description = "会计年度")
    private Integer fiscalYear;

    @Schema(description = "应纳税所得额")
    private BigDecimal taxableIncome;

    @Schema(description = "税率")
    private BigDecimal taxRate;

    @Schema(description = "应纳税额")
    private BigDecimal taxAmount;

    @Schema(description = "已预缴金额")
    private BigDecimal prepaidAmount;

    @Schema(description = "应补税额")
    private BigDecimal payableAmount;

    @Schema(description = "应退税额")
    private BigDecimal refundAmount;

    @Schema(description = "汇算清缴日期")
    private LocalDate settlementDate;

    @Schema(description = "计算日期")
    private LocalDate calculateDate;

    @Schema(description = "备注")
    private String remark;
}

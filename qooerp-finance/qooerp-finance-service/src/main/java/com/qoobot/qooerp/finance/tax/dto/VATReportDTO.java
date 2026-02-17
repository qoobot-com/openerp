package com.qoobot.qooerp.finance.tax.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 增值税报表DTO
 *
 * @author QooERP Team
 * @since 2026-02-17
 */
@Data
@Schema(description = "增值税报表DTO")
public class VATReportDTO {

    @Schema(description = "VAT记录ID")
    private Long id;

    @Schema(description = "会计年度")
    private Integer fiscalYear;

    @Schema(description = "期间")
    private Integer periodNo;

    @Schema(description = "进项税额")
    private BigDecimal inputTax;

    @Schema(description = "销项税额")
    private BigDecimal outputTax;

    @Schema(description = "应交增值税")
    private BigDecimal payableVAT;

    @Schema(description = "已交增值税")
    private BigDecimal paidVAT;

    @Schema(description = "未交增值税")
    private BigDecimal unpaidVAT;

    @Schema(description = "计算日期")
    private LocalDate calculateDate;

    @Schema(description = "备注")
    private String remark;
}

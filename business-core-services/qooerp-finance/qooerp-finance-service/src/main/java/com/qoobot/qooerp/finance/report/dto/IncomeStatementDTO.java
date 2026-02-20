package com.qoobot.qooerp.finance.report.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 利润表DTO
 *
 * @author QooERP Team
 * @since 2026-02-17
 */
@Data
@Schema(description = "利润表DTO")
public class IncomeStatementDTO {

    @Schema(description = "报表ID")
    private Long id;

    @Schema(description = "会计年度")
    private Integer fiscalYear;

    @Schema(description = "期间")
    private Integer periodNo;

    @Schema(description = "报表日期")
    private Date reportDate;

    @Schema(description = "营业收入")
    private BigDecimal operatingRevenue;

    @Schema(description = "营业成本")
    private BigDecimal operatingCost;

    @Schema(description = "销售费用")
    private BigDecimal sellingExpenses;

    @Schema(description = "管理费用")
    private BigDecimal administrativeExpenses;

    @Schema(description = "财务费用")
    private BigDecimal financialExpenses;

    @Schema(description = "营业利润")
    private BigDecimal operatingProfit;

    @Schema(description = "营业外收入")
    private BigDecimal nonOperatingIncome;

    @Schema(description = "营业外支出")
    private BigDecimal nonOperatingExpenses;

    @Schema(description = "利润总额")
    private BigDecimal totalProfit;

    @Schema(description = "所得税费用")
    private BigDecimal incomeTaxExpense;

    @Schema(description = "净利润")
    private BigDecimal netProfit;

    @Schema(description = "总收入")
    private BigDecimal totalRevenue;

    @Schema(description = "总成本费用")
    private BigDecimal totalCostAndExpenses;

    @Schema(description = "创建时间")
    private Date createTime;
}

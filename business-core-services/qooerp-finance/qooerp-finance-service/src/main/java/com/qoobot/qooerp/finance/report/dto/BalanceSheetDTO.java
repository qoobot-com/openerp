package com.qoobot.qooerp.finance.report.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 资产负债表DTO
 *
 * @author QooERP Team
 * @since 20xx-xx-xx
 */
@Data
@Schema(description = "资产负债表DTO")
public class BalanceSheetDTO {

    @Schema(description = "报表ID")
    private Long id;

    @Schema(description = "会计年度")
    private Integer fiscalYear;

    @Schema(description = "期间")
    private Integer periodNo;

    @Schema(description = "报表日期")
    private Date reportDate;

    @Schema(description = "资产总额")
    private BigDecimal totalAssets;

    @Schema(description = "负债总额")
    private BigDecimal totalLiabilities;

    @Schema(description = "所有者权益总额")
    private BigDecimal totalEquity;

    @Schema(description = "负债及所有者权益总计")
    private BigDecimal totalLiabilitiesAndEquity;

    @Schema(description = "货币资金")
    private BigDecimal monetaryFunds;

    @Schema(description = "应收账款")
    private BigDecimal accountsReceivable;

    @Schema(description = "存货")
    private BigDecimal inventory;

    @Schema(description = "固定资产原值")
    private BigDecimal fixedAssetsOriginalValue;

    @Schema(description = "累计折旧")
    private BigDecimal accumulatedDepreciation;

    @Schema(description = "固定资产净值")
    private BigDecimal fixedAssetsNetValue;

    @Schema(description = "应付账款")
    private BigDecimal accountsPayable;

    @Schema(description = "实收资本")
    private BigDecimal paidInCapital;

    @Schema(description = "未分配利润")
    private BigDecimal retainedEarnings;

    @Schema(description = "创建时间")
    private Date createTime;
}

package com.qoobot.qooerp.finance.report.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 利润表实体
 * 
 * @author QooERP Team
 * @since 2026-02-17
 */
@Data
@TableName("finance_income_statement")
public class FinanceIncomeStatement {

    /**
     * 利润表ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 会计年度
     */
    private Integer year;

    /**
     * 会计期间
     */
    private Integer period;

    /**
     * 报表期间开始日期
     */
    private LocalDate periodStart;

    /**
     * 报表期间结束日期
     */
    private LocalDate periodEnd;

    /**
     * 营业收入
     */
    private BigDecimal operatingRevenue;

    /**
     * 营业成本
     */
    private BigDecimal operatingCost;

    /**
     * 营业税金及附加
     */
    private BigDecimal taxAndSurcharges;

    /**
     * 销售费用
     */
    private BigDecimal sellingExpenses;

    /**
     * 管理费用
     */
    private BigDecimal administrativeExpenses;

    /**
     * 财务费用
     */
    private BigDecimal financialExpenses;

    /**
     * 其他收益
     */
    private BigDecimal otherIncome;

    /**
     * 投资收益
     */
    private BigDecimal investmentIncome;

    /**
     * 营业外收入
     */
    private BigDecimal nonOperatingIncome;

    /**
     * 营业外支出
     */
    private BigDecimal nonOperatingExpenses;

    /**
     * 利润总额
     */
    private BigDecimal totalProfit;

    /**
     * 所得税费用
     */
    private BigDecimal incomeTaxExpense;

    /**
     * 净利润
     */
    private BigDecimal netProfit;

    /**
     * 归属于母公司所有者的净利润
     */
    private BigDecimal parentCompanyNetProfit;

    /**
     * 少数股东损益
     */
    private BigDecimal minorityInterest;

    /**
     * 基本每股收益
     */
    private BigDecimal basicEps;

    /**
     * 稀释每股收益
     */
    private BigDecimal dilutedEps;

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
     * 租户ID
     */
    private Long tenantId;
}

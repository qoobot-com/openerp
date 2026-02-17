package com.qoobot.qooerp.finance.report.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 资产负债表
 */
@Data
@TableName("finance_balance_sheet")
public class FinanceBalanceSheet {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 报表日期
     */
    private LocalDate reportDate;

    /**
     * 科目编码
     */
    private String accountCode;

    /**
     * 科目名称
     */
    private String accountName;

    /**
     * 资产类别（0-流动资产 1-非流动资产 2-流动负债 3-非流动负债 4-所有者权益）
     */
    private Integer category;

    /**
     * 期初余额
     */
    private BigDecimal beginningBalance;

    /**
     * 本期借方发生额
     */
    private BigDecimal debitAmount;

    /**
     * 本期贷方发生额
     */
    private BigDecimal creditAmount;

    /**
     * 期末余额
     */
    private BigDecimal endingBalance;

    /**
     * 占比
     */
    private BigDecimal percentage;

    /**
     * 层级
     */
    private Integer level;

    /**
     * 排序
     */
    private Integer sortOrder;
}

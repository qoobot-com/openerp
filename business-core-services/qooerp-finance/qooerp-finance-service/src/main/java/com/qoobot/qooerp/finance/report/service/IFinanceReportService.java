package com.qoobot.qooerp.finance.report.service;

import com.qoobot.qooerp.finance.report.domain.FinanceBalanceSheet;
import com.qoobot.qooerp.finance.report.domain.FinanceIncomeStatement;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 财务报表服务接口
 *
 * @author QooERP Team
 * @since 20xx-xx-xx
 */
public interface IFinanceReportService {

    /**
     * 生成资产负债表
     *
     * @param fiscalYear 会计年度
     * @param periodNo 期间
     * @return 资产负债表
     */
    FinanceBalanceSheet generateBalanceSheet(Integer fiscalYear, Integer periodNo);

    /**
     * 生成利润表
     *
     * @param fiscalYear 会计年度
     * @param periodNo 期间
     * @return 利润表
     */
    FinanceIncomeStatement generateIncomeStatement(Integer fiscalYear, Integer periodNo);

    /**
     * 生成现金流量表
     *
     * @param fiscalYear 会计年度
     * @param periodNo 期间
     * @return 现金流量表数据
     */
    Map<String, Object> generateCashFlowStatement(Integer fiscalYear, Integer periodNo);

    /**
     * 生成合并报表
     *
     * @param fiscalYear 会计年度
     * @param periodNo 期间
     * @param companyIds 公司ID列表
     * @return 合并报表数据
     */
    Map<String, Object> generateConsolidatedReport(Integer fiscalYear, Integer periodNo, List<Long> companyIds);

    /**
     * 查询资产负债表
     *
     * @param fiscalYear 会计年度
     * @param periodNo 期间
     * @return 资产负债表
     */
    FinanceBalanceSheet getBalanceSheet(Integer fiscalYear, Integer periodNo);

    /**
     * 查询利润表
     *
     * @param fiscalYear 会计年度
     * @param periodNo 期间
     * @return 利润表
     */
    FinanceIncomeStatement getIncomeStatement(Integer fiscalYear, Integer periodNo);

    /**
     * 报表分析
     *
     * @param fiscalYear 会计年度
     * @param periodNo 期间
     * @return 报表分析数据
     */
    Map<String, Object> analyzeReports(Integer fiscalYear, Integer periodNo);
}

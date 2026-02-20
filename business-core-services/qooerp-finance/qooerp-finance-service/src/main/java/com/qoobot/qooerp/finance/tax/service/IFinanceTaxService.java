package com.qoobot.qooerp.finance.tax.service;

import com.qoobot.qooerp.finance.invoice.domain.FinanceInvoice;
import com.qoobot.qooerp.finance.report.domain.FinanceIncomeStatement;
import com.qoobot.qooerp.finance.tax.domain.FinanceCIT;
import com.qoobot.qooerp.finance.tax.domain.FinancePIT;
import com.qoobot.qooerp.finance.tax.domain.FinanceVAT;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 税务服务接口
 *
 * @author QooERP Team
 * @since 20xx-xx-xx
 */
public interface IFinanceTaxService {

    /**
     * 计算增值税
     *
     * @param fiscalYear 会计年度
     * @param periodNo 期间
     * @return 增值税信息
     */
    FinanceVAT calculateVAT(Integer fiscalYear, Integer periodNo);

    /**
     * 计算企业所得税
     *
     * @param incomeStatement 利润表
     * @return 企业所得税信息
     */
    FinanceCIT calculateCIT(FinanceIncomeStatement incomeStatement);

    /**
     * 计算个人所得税
     *
     * @param employeeId 员工ID
     * @param salary 收入
     * @param deductions 扣除项
     * @return 个人所得税信息
     */
    FinancePIT calculatePIT(Long employeeId, BigDecimal salary, BigDecimal deductions);

    /**
     * 根据发票计算增值税
     *
     * @param invoices 发票列表
     * @return 增值税计算结果
     */
    Map<String, BigDecimal> calculateVATFromInvoices(List<FinanceInvoice> invoices);

    /**
     * 预缴企业所得税
     *
     * @param fiscalYear 会计年度
     * @param periodNo 期间
     * @param amount 预缴金额
     */
    void prepayCIT(Integer fiscalYear, Integer periodNo, BigDecimal amount);

    /**
     * 汇算清缴企业所得税
     *
     * @param fiscalYear 会计年度
     * @return 清缴结果
     */
    Map<String, Object> settleCIT(Integer fiscalYear);

    /**
     * 查询增值税记录
     *
     * @param fiscalYear 会计年度
     * @param periodNo 期间
     * @return 增值税记录
     */
    FinanceVAT getVATRecord(Integer fiscalYear, Integer periodNo);

    /**
     * 查询企业所得税记录
     *
     * @param fiscalYear 会计年度
     * @return 企业所得税记录
     */
    FinanceCIT getCITRecord(Integer fiscalYear);

    /**
     * 查询个人所得税记录
     *
     * @param employeeId 员工ID
     * @param fiscalYear 会计年度
     * @return 个人所得税记录列表
     */
    List<FinancePIT> getPITRecords(Long employeeId, Integer fiscalYear);
}

package com.qoobot.qooerp.finance.report.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qoobot.qooerp.common.exception.BusinessException;
import com.qoobot.qooerp.finance.accounting.domain.FinanceAccount;
import com.qoobot.qooerp.finance.accounting.service.IFinanceAccountService;
import com.qoobot.qooerp.finance.period.domain.FinancePeriod;
import com.qoobot.qooerp.finance.period.service.IFinancePeriodService;
import com.qoobot.qooerp.finance.report.domain.FinanceBalanceSheet;
import com.qoobot.qooerp.finance.report.domain.FinanceIncomeStatement;
import com.qoobot.qooerp.finance.report.mapper.FinanceBalanceSheetMapper;
import com.qoobot.qooerp.finance.report.mapper.FinanceIncomeStatementMapper;
import com.qoobot.qooerp.finance.report.service.IFinanceReportService;
import com.qoobot.qooerp.finance.voucher.domain.FinanceVoucher;
import com.qoobot.qooerp.finance.voucher.service.IFinanceVoucherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

/**
 * 财务报表服务实现类
 *
 * @author QooERP Team
 * @since 20xx-xx-xx
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FinanceReportServiceImpl implements IFinanceReportService {

    private final FinanceBalanceSheetMapper balanceSheetMapper;
    private final FinanceIncomeStatementMapper incomeStatementMapper;
    private final IFinanceAccountService accountService;
    private final IFinanceVoucherService voucherService;
    private final IFinancePeriodService periodService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FinanceBalanceSheet generateBalanceSheet(Integer fiscalYear, Integer periodNo) {
        // 检查期间是否已结账
        FinancePeriod period = periodService.getPeriodsByYear(fiscalYear).stream()
                .filter(p -> p.getPeriodNo().equals(periodNo))
                .findFirst()
                .orElseThrow(() -> new BusinessException("会计期间不存在"));

        if (period.getStatus() != 2) {
            throw new BusinessException("会计期间未结账，无法生成报表");
        }

        FinanceBalanceSheet balanceSheet = new FinanceBalanceSheet();
        balanceSheet.setReportDate(period.getEndDate());

        // 生成资产负债表数据
        generateBalanceSheetData(balanceSheet, fiscalYear, periodNo);

        log.info("生成资产负债表成功，年度: {}, 期间: {}", fiscalYear, periodNo);

        return balanceSheet;
    }

    /**
     * 生成资产负债表数据
     */
    private void generateBalanceSheetData(FinanceBalanceSheet balanceSheet, Integer fiscalYear, Integer periodNo) {
        List<FinanceAccount> allAccounts = accountService.list();

        for (FinanceAccount account : allAccounts) {
            FinanceBalanceSheet item = new FinanceBalanceSheet();
            item.setReportDate(balanceSheet.getReportDate());
            item.setAccountCode(account.getAccountCode());
            item.setAccountName(account.getAccountName());
            item.setCategory(getCategoryByAccountType(account.getAccountType()));
            item.setBeginningBalance(BigDecimal.ZERO);
            item.setDebitAmount(BigDecimal.ZERO);
            item.setCreditAmount(BigDecimal.ZERO);
            item.setEndingBalance(BigDecimal.ZERO);
            item.setLevel(1);
            item.setSortOrder(0);

            balanceSheetMapper.insert(item);
        }
    }

    /**
     * 根据科目类型获取类别
     */
    private Integer getCategoryByAccountType(String accountType) {
        switch (accountType) {
            case "ASSET":
                return 0; // 流动资产
            case "FIXED_ASSET":
                return 1; // 非流动资产
            case "LIABILITY":
                return 2; // 流动负债
            case "LONG_TERM_LIABILITY":
                return 3; // 非流动负债
            case "EQUITY":
                return 4; // 所有者权益
            default:
                return 0;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FinanceIncomeStatement generateIncomeStatement(Integer fiscalYear, Integer periodNo) {
        // 检查期间是否已结账
        FinancePeriod period = periodService.getPeriodsByYear(fiscalYear).stream()
                .filter(p -> p.getPeriodNo().equals(periodNo))
                .findFirst()
                .orElseThrow(() -> new BusinessException("会计期间不存在"));

        if (period.getStatus() != 2) {
            throw new BusinessException("会计期间未结账，无法生成报表");
        }

        FinanceIncomeStatement incomeStatement = new FinanceIncomeStatement();
        incomeStatement.setYear(fiscalYear);
        incomeStatement.setPeriod(periodNo);
        incomeStatement.setPeriodStart(period.getStartDate());
        incomeStatement.setPeriodEnd(period.getEndDate());

        // 汇总收入科目（科目类型为REVENUE）
        List<FinanceAccount> revenueAccounts = accountService.listByType("REVENUE");
        BigDecimal operatingRevenue = calculateAccountBalance(revenueAccounts, fiscalYear, periodNo, period.getEndDate());
        incomeStatement.setOperatingRevenue(operatingRevenue);

        // 汇总成本科目（科目类型为COST）
        List<FinanceAccount> costAccounts = accountService.listByType("COST");
        BigDecimal operatingCost = calculateAccountBalance(costAccounts, fiscalYear, periodNo, period.getEndDate());
        incomeStatement.setOperatingCost(operatingCost);

        // 汇总费用科目（科目类型为EXPENSE）
        List<FinanceAccount> expenseAccounts = accountService.listByType("EXPENSE");
        BigDecimal totalExpense = calculateAccountBalance(expenseAccounts, fiscalYear, periodNo, period.getEndDate());
        incomeStatement.setSellingExpenses(BigDecimal.ZERO);
        incomeStatement.setAdministrativeExpenses(BigDecimal.ZERO);
        incomeStatement.setFinancialExpenses(totalExpense);

        // 计算利润总额
        BigDecimal totalProfit = operatingRevenue.subtract(operatingCost).subtract(totalExpense);
        incomeStatement.setTotalProfit(totalProfit);

        // 简化处理，假设利润总额即为净利润
        incomeStatement.setNetProfit(totalProfit);

        incomeStatement.setCreateTime(java.time.LocalDateTime.now());

        incomeStatementMapper.insert(incomeStatement);
        log.info("生成利润表成功，年度: {}, 期间: {}", fiscalYear, periodNo);

        return incomeStatement;
    }

    @Override
    public Map<String, Object> generateCashFlowStatement(Integer fiscalYear, Integer periodNo) {
        // 检查期间
        FinancePeriod period = periodService.getPeriodsByYear(fiscalYear).stream()
                .filter(p -> p.getPeriodNo().equals(periodNo))
                .findFirst()
                .orElseThrow(() -> new BusinessException("会计期间不存在"));

        Map<String, Object> cashFlow = new HashMap<>();
        cashFlow.put("fiscalYear", fiscalYear);
        cashFlow.put("periodNo", periodNo);
        cashFlow.put("reportDate", period.getEndDate());

        // 简化处理，返回模拟数据
        cashFlow.put("operatingCashFlow", BigDecimal.ZERO);
        cashFlow.put("investingCashFlow", BigDecimal.ZERO);
        cashFlow.put("financingCashFlow", BigDecimal.ZERO);
        cashFlow.put("netCashFlow", BigDecimal.ZERO);
        cashFlow.put("cashBalanceAtStart", BigDecimal.ZERO);
        cashFlow.put("cashBalanceAtEnd", BigDecimal.ZERO);

        log.info("生成现金流量表成功，年度: {}, 期间: {}", fiscalYear, periodNo);

        return cashFlow;
    }

    @Override
    public Map<String, Object> generateConsolidatedReport(Integer fiscalYear, Integer periodNo, List<Long> companyIds) {
        Map<String, Object> consolidatedReport = new HashMap<>();
        consolidatedReport.put("fiscalYear", fiscalYear);
        consolidatedReport.put("periodNo", periodNo);
        consolidatedReport.put("companyIds", companyIds);

        // 合并资产负债表
        FinanceBalanceSheet balanceSheet = getBalanceSheet(fiscalYear, periodNo);
        consolidatedReport.put("balanceSheet", balanceSheet);

        // 合并利润表
        FinanceIncomeStatement incomeStatement = getIncomeStatement(fiscalYear, periodNo);
        consolidatedReport.put("incomeStatement", incomeStatement);

        // 合并现金流量表
        Map<String, Object> cashFlow = generateCashFlowStatement(fiscalYear, periodNo);
        consolidatedReport.put("cashFlowStatement", cashFlow);

        log.info("生成合并报表成功，年度: {}, 期间: {}, 公司数: {}", fiscalYear, periodNo, companyIds.size());

        return consolidatedReport;
    }

    @Override
    public FinanceBalanceSheet getBalanceSheet(Integer fiscalYear, Integer periodNo) {
        LambdaQueryWrapper<FinanceBalanceSheet> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(FinanceBalanceSheet::getReportDate);
        wrapper.last("LIMIT 1");
        return balanceSheetMapper.selectOne(wrapper);
    }

    @Override
    public FinanceIncomeStatement getIncomeStatement(Integer fiscalYear, Integer periodNo) {
        LambdaQueryWrapper<FinanceIncomeStatement> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FinanceIncomeStatement::getYear, fiscalYear)
                .eq(FinanceIncomeStatement::getPeriod, periodNo)
                .last("LIMIT 1");
        return incomeStatementMapper.selectOne(wrapper);
    }

    @Override
    public Map<String, Object> analyzeReports(Integer fiscalYear, Integer periodNo) {
        Map<String, Object> analysis = new HashMap<>();

        // 获取报表
        FinanceIncomeStatement incomeStatement = getIncomeStatement(fiscalYear, periodNo);

        if (incomeStatement != null) {
            // 计算销售净利率
            if (incomeStatement.getOperatingRevenue() != null &&
                incomeStatement.getOperatingRevenue().compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal netProfitMargin = incomeStatement.getNetProfit()
                        .divide(incomeStatement.getOperatingRevenue(), 4, java.math.RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100));
                analysis.put("netProfitMargin", netProfitMargin);
            }

            // 计算毛利率
            if (incomeStatement.getOperatingRevenue() != null &&
                incomeStatement.getOperatingRevenue().compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal grossProfit = incomeStatement.getOperatingRevenue()
                        .subtract(incomeStatement.getOperatingCost() != null ?
                                incomeStatement.getOperatingCost() : BigDecimal.ZERO);
                BigDecimal grossMargin = grossProfit
                        .divide(incomeStatement.getOperatingRevenue(), 4, java.math.RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100));
                analysis.put("grossMargin", grossMargin);
            }

            // 净利润
            analysis.put("netProfit", incomeStatement.getNetProfit());
        }

        analysis.put("fiscalYear", fiscalYear);
        analysis.put("periodNo", periodNo);

        return analysis;
    }

    /**
     * 计算科目余额
     * 简化实现：需要根据实际凭证计算科目余额
     */
    private BigDecimal calculateAccountBalance(List<FinanceAccount> accounts, Integer fiscalYear,
                                              Integer periodNo, LocalDate endDate) {
        BigDecimal totalBalance = BigDecimal.ZERO;

        // TODO: 需要根据凭证分录计算实际余额
        // 这里简化处理，返回模拟数据
        for (FinanceAccount account : accounts) {
            totalBalance = totalBalance.add(BigDecimal.ZERO);
        }

        return totalBalance;
    }
}

package com.qoobot.qooerp.finance.tax.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qoobot.qooerp.common.exception.BusinessException;
import com.qoobot.qooerp.finance.invoice.domain.FinanceInvoice;
import com.qoobot.qooerp.finance.invoice.service.IFinanceInvoiceService;
import com.qoobot.qooerp.finance.report.domain.FinanceIncomeStatement;
import com.qoobot.qooerp.finance.tax.domain.FinanceCIT;
import com.qoobot.qooerp.finance.tax.domain.FinancePIT;
import com.qoobot.qooerp.finance.tax.domain.FinanceVAT;
import com.qoobot.qooerp.finance.tax.mapper.FinanceCITMapper;
import com.qoobot.qooerp.finance.tax.mapper.FinancePITMapper;
import com.qoobot.qooerp.finance.tax.mapper.FinanceVATMapper;
import com.qoobot.qooerp.finance.tax.service.IFinanceTaxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;

/**
 * 税务服务实现类
 *
 * @author QooERP Team
 * @since 2026-02-17
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FinanceTaxServiceImpl implements IFinanceTaxService {

    private final FinanceVATMapper vatMapper;
    private final FinanceCITMapper citMapper;
    private final FinancePITMapper pitMapper;
    private final IFinanceInvoiceService invoiceService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FinanceVAT calculateVAT(Integer fiscalYear, Integer periodNo) {
        // 查询该期间的所有已认证发票
        LocalDate startDate = LocalDate.of(fiscalYear, periodNo, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        List<FinanceInvoice> salesInvoices = invoiceService.queryInvoicesByDateRange(startDate, endDate, "SALES");
        List<FinanceInvoice> purchaseInvoices = invoiceService.queryInvoicesByDateRange(startDate, endDate, "PURCHASE");

        // 计算进项税额和销项税额
        BigDecimal inputVAT = BigDecimal.ZERO;
        BigDecimal inputVATCertified = BigDecimal.ZERO;
        for (FinanceInvoice invoice : purchaseInvoices) {
            if ("VERIFIED".equals(invoice.getStatus()) && invoice.getTaxAmount() != null) {
                inputVAT = inputVAT.add(invoice.getTaxAmount());
                inputVATCertified = inputVATCertified.add(invoice.getTaxAmount());
            }
        }

        BigDecimal outputVAT = BigDecimal.ZERO;
        for (FinanceInvoice invoice : salesInvoices) {
            if ("VERIFIED".equals(invoice.getStatus()) && invoice.getTaxAmount() != null) {
                outputVAT = outputVAT.add(invoice.getTaxAmount());
            }
        }

        // 计算应交增值税
        BigDecimal payableVAT = outputVAT.subtract(inputVATCertified);
        if (payableVAT.compareTo(BigDecimal.ZERO) < 0) {
            payableVAT = BigDecimal.ZERO; // 进项税大于销项税，无需缴纳
        }

        // 创建或更新VAT记录
        FinanceVAT vat = getVATRecord(fiscalYear, periodNo);
        if (vat == null) {
            vat = new FinanceVAT();
            vat.setTaxPeriod(fiscalYear + "-" + String.format("%02d", periodNo));
        }

        vat.setInputVAT(inputVAT);
        vat.setInputVATCertified(inputVATCertified);
        vat.setOutputVAT(outputVAT);
        vat.setPayableVAT(payableVAT);
        vat.setUnpaidVAT(payableVAT);

        if (vat.getId() == null) {
            vatMapper.insert(vat);
        } else {
            vatMapper.updateById(vat);
        }

        log.info("计算增值税成功，年度: {}, 期间: {}, 进项税: {}, 销项税: {}, 应交: {}",
                fiscalYear, periodNo, inputVAT, outputVAT, payableVAT);

        return vat;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FinanceCIT calculateCIT(FinanceIncomeStatement incomeStatement) {
        if (incomeStatement == null || incomeStatement.getNetProfit() == null) {
            throw new BusinessException("利润表数据不完整");
        }

        // 企业所得税税率（假设为25%）
        BigDecimal taxRate = BigDecimal.valueOf(0.25);

        // 计算应纳税所得额（简化处理，直接使用利润）
        BigDecimal taxableIncome = incomeStatement.getNetProfit();

        // 计算应纳税额
        BigDecimal taxLiability = taxableIncome.multiply(taxRate)
                .setScale(2, RoundingMode.HALF_UP);

        // 如果利润为负，不交税
        if (taxableIncome.compareTo(BigDecimal.ZERO) < 0) {
            taxLiability = BigDecimal.ZERO;
        }

        // 创建或更新CIT记录
        FinanceCIT cit = getCITRecord(incomeStatement.getYear());
        if (cit == null) {
            cit = new FinanceCIT();
            cit.setTaxYear(incomeStatement.getYear());
            cit.setTaxPeriodType("ANNUAL");
            cit.setStatus("DRAFT");
        }

        cit.setTaxableIncome(taxableIncome);
        cit.setTaxRate(taxRate.multiply(BigDecimal.valueOf(100)));
        cit.setTaxLiability(taxLiability);
        cit.setTaxPayable(taxLiability);

        if (cit.getId() == null) {
            citMapper.insert(cit);
        } else {
            citMapper.updateById(cit);
        }

        log.info("计算企业所得税成功，年度: {}, 应纳税所得额: {}, 税额: {}",
                incomeStatement.getYear(), taxableIncome, taxLiability);

        return cit;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FinancePIT calculatePIT(Long employeeId, BigDecimal salary, BigDecimal deductions) {
        // 个人所得税起征点（5000元）
        BigDecimal threshold = BigDecimal.valueOf(5000);

        // 计算应纳税所得额
        BigDecimal taxableIncome = salary.subtract(threshold);
        if (deductions != null) {
            taxableIncome = taxableIncome.subtract(deductions);
        }

        // 如果应纳税所得额小于等于0，不交税
        BigDecimal taxLiability = BigDecimal.ZERO;
        BigDecimal quickDeduction = BigDecimal.ZERO;
        if (taxableIncome.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal[] result = calculatePITByBracket(taxableIncome);
            taxLiability = result[0];
            quickDeduction = result[1];
        }

        // 创建PIT记录
        FinancePIT pit = new FinancePIT();
        pit.setEmployeeId(employeeId);
        pit.setTaxYear(LocalDate.now().getYear());
        pit.setMonth(LocalDate.now().getMonthValue());
        pit.setGrossSalary(salary);
        pit.setSpecialDeduction(deductions != null ? deductions : BigDecimal.ZERO);
        pit.setThreshold(threshold);
        pit.setTaxableIncome(taxableIncome.compareTo(BigDecimal.ZERO) > 0 ? taxableIncome : BigDecimal.ZERO);
        pit.setTaxLiability(taxLiability);
        pit.setQuickDeduction(quickDeduction);
        pit.setTaxPayable(taxLiability);
        pit.setStatus("DRAFT");

        pitMapper.insert(pit);

        log.info("计算个人所得税成功，员工ID: {}, 收入: {}, 税额: {}", employeeId, salary, taxLiability);

        return pit;
    }

    @Override
    public Map<String, BigDecimal> calculateVATFromInvoices(List<FinanceInvoice> invoices) {
        Map<String, BigDecimal> result = new HashMap<>();
        BigDecimal inputVAT = BigDecimal.ZERO;
        BigDecimal outputVAT = BigDecimal.ZERO;

        for (FinanceInvoice invoice : invoices) {
            if ("PURCHASE".equals(invoice.getInvoiceType()) && "VERIFIED".equals(invoice.getStatus())) {
                inputVAT = inputVAT.add(invoice.getTaxAmount() != null ? invoice.getTaxAmount() : BigDecimal.ZERO);
            } else if ("SALES".equals(invoice.getInvoiceType()) && "VERIFIED".equals(invoice.getStatus())) {
                outputVAT = outputVAT.add(invoice.getTaxAmount() != null ? invoice.getTaxAmount() : BigDecimal.ZERO);
            }
        }

        BigDecimal payableVAT = outputVAT.subtract(inputVAT);
        if (payableVAT.compareTo(BigDecimal.ZERO) < 0) {
            payableVAT = BigDecimal.ZERO;
        }

        result.put("inputVAT", inputVAT);
        result.put("outputVAT", outputVAT);
        result.put("payableVAT", payableVAT);

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void prepayCIT(Integer fiscalYear, Integer periodNo, BigDecimal amount) {
        FinanceCIT cit = getCITRecord(fiscalYear);
        if (cit == null) {
            cit = new FinanceCIT();
            cit.setTaxYear(fiscalYear);
            cit.setQuarter((periodNo - 1) / 3 + 1);
            cit.setTaxPeriodType("QUARTER");
            cit.setStatus("DRAFT");
        }

        BigDecimal prepaidTax = cit.getPrepaidTax() != null
                ? cit.getPrepaidTax() : BigDecimal.ZERO;
        cit.setPrepaidTax(prepaidTax.add(amount));

        if (cit.getId() == null) {
            citMapper.insert(cit);
        } else {
            citMapper.updateById(cit);
        }

        log.info("预缴企业所得税成功，年度: {}, 期间: {}, 金额: {}", fiscalYear, periodNo, amount);
    }

    @Override
    public Map<String, Object> settleCIT(Integer fiscalYear) {
        FinanceCIT cit = getCITRecord(fiscalYear);
        if (cit == null) {
            throw new BusinessException("企业所得税记录不存在");
        }

        BigDecimal prepaidTax = cit.getPrepaidTax() != null
                ? cit.getPrepaidTax() : BigDecimal.ZERO;
        BigDecimal taxLiability = cit.getTaxLiability() != null
                ? cit.getTaxLiability() : BigDecimal.ZERO;
        BigDecimal taxPayable = taxLiability.subtract(prepaidTax);

        if (taxPayable.compareTo(BigDecimal.ZERO) < 0) {
            taxPayable = BigDecimal.ZERO; // 预缴超过应缴
        }

        cit.setTaxPayable(taxPayable);
        cit.setStatus("SUBMITTED");
        citMapper.updateById(cit);

        Map<String, Object> result = new HashMap<>();
        result.put("fiscalYear", fiscalYear);
        result.put("prepaidTax", prepaidTax);
        result.put("taxLiability", taxLiability);
        result.put("taxPayable", taxPayable);

        log.info("汇算清缴企业所得税完成，年度: {}, 应补税: {}",
                fiscalYear, taxPayable);

        return result;
    }

    @Override
    public FinanceVAT getVATRecord(Integer fiscalYear, Integer periodNo) {
        LambdaQueryWrapper<FinanceVAT> wrapper = new LambdaQueryWrapper<>();
        String taxPeriod = fiscalYear + "-" + String.format("%02d", periodNo);
        wrapper.eq(FinanceVAT::getTaxPeriod, taxPeriod)
                .last("LIMIT 1");
        return vatMapper.selectOne(wrapper);
    }

    @Override
    public FinanceCIT getCITRecord(Integer fiscalYear) {
        LambdaQueryWrapper<FinanceCIT> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FinanceCIT::getTaxYear, fiscalYear)
                .last("LIMIT 1");
        return citMapper.selectOne(wrapper);
    }

    @Override
    public List<FinancePIT> getPITRecords(Long employeeId, Integer fiscalYear) {
        LambdaQueryWrapper<FinancePIT> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FinancePIT::getEmployeeId, employeeId)
                .eq(FinancePIT::getTaxYear, fiscalYear)
                .orderByDesc(FinancePIT::getCreateTime);
        return pitMapper.selectList(wrapper);
    }

    /**
     * 按税率档次计算个人所得税
     * 简化实现，使用2019年新税法计算
     * 返回数组：[0]税额, [1]速算扣除数
     */
    private BigDecimal[] calculatePITByBracket(BigDecimal taxableIncome) {
        // 七级超额累进税率
        if (taxableIncome.compareTo(BigDecimal.valueOf(3000)) <= 0) {
            return new BigDecimal[]{
                    taxableIncome.multiply(BigDecimal.valueOf(0.03)),
                    BigDecimal.ZERO
            };
        } else if (taxableIncome.compareTo(BigDecimal.valueOf(12000)) <= 0) {
            return new BigDecimal[]{
                    taxableIncome.multiply(BigDecimal.valueOf(0.10)).subtract(BigDecimal.valueOf(210)),
                    BigDecimal.valueOf(210)
            };
        } else if (taxableIncome.compareTo(BigDecimal.valueOf(25000)) <= 0) {
            return new BigDecimal[]{
                    taxableIncome.multiply(BigDecimal.valueOf(0.20)).subtract(BigDecimal.valueOf(1410)),
                    BigDecimal.valueOf(1410)
            };
        } else if (taxableIncome.compareTo(BigDecimal.valueOf(35000)) <= 0) {
            return new BigDecimal[]{
                    taxableIncome.multiply(BigDecimal.valueOf(0.25)).subtract(BigDecimal.valueOf(2660)),
                    BigDecimal.valueOf(2660)
            };
        } else if (taxableIncome.compareTo(BigDecimal.valueOf(55000)) <= 0) {
            return new BigDecimal[]{
                    taxableIncome.multiply(BigDecimal.valueOf(0.30)).subtract(BigDecimal.valueOf(4410)),
                    BigDecimal.valueOf(4410)
            };
        } else if (taxableIncome.compareTo(BigDecimal.valueOf(80000)) <= 0) {
            return new BigDecimal[]{
                    taxableIncome.multiply(BigDecimal.valueOf(0.35)).subtract(BigDecimal.valueOf(7160)),
                    BigDecimal.valueOf(7160)
            };
        } else {
            return new BigDecimal[]{
                    taxableIncome.multiply(BigDecimal.valueOf(0.45)).subtract(BigDecimal.valueOf(15160)),
                    BigDecimal.valueOf(15160)
            };
        }
    }
}

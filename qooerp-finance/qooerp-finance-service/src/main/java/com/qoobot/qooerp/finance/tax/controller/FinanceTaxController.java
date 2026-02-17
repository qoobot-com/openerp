package com.qoobot.qooerp.finance.tax.controller;

import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.finance.report.domain.FinanceIncomeStatement;
import com.qoobot.qooerp.finance.tax.domain.FinanceCIT;
import com.qoobot.qooerp.finance.tax.domain.FinancePIT;
import com.qoobot.qooerp.finance.tax.domain.FinanceVAT;
import com.qoobot.qooerp.finance.tax.service.IFinanceTaxService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 税务管理控制器
 *
 * @author QooERP Team
 * @since 2026-02-17
 */
@Tag(name = "税务管理", description = "税务相关接口")
@RestController
@RequestMapping("/finance/tax")
@RequiredArgsConstructor
public class FinanceTaxController {

    private final IFinanceTaxService taxService;

    @Operation(summary = "计算增值税")
    @PostMapping("/vat/calculate")
    public Result<FinanceVAT> calculateVAT(
            @Parameter(description = "会计年度", required = true) @RequestParam Integer fiscalYear,
            @Parameter(description = "期间", required = true) @RequestParam Integer periodNo) {
        FinanceVAT vat = taxService.calculateVAT(fiscalYear, periodNo);
        return Result.success(vat);
    }

    @Operation(summary = "计算企业所得税")
    @PostMapping("/cit/calculate")
    public Result<FinanceCIT> calculateCIT(@RequestBody FinanceIncomeStatement incomeStatement) {
        FinanceCIT cit = taxService.calculateCIT(incomeStatement);
        return Result.success(cit);
    }

    @Operation(summary = "计算个人所得税")
    @PostMapping("/pit/calculate")
    public Result<FinancePIT> calculatePIT(
            @Parameter(description = "员工ID", required = true) @RequestParam Long employeeId,
            @Parameter(description = "收入", required = true) @RequestParam BigDecimal salary,
            @Parameter(description = "扣除项") @RequestParam(required = false) BigDecimal deductions) {
        FinancePIT pit = taxService.calculatePIT(employeeId, salary, deductions);
        return Result.success(pit);
    }

    @Operation(summary = "预缴企业所得税")
    @PostMapping("/cit/prepay")
    public Result<Void> prepayCIT(
            @Parameter(description = "会计年度", required = true) @RequestParam Integer fiscalYear,
            @Parameter(description = "期间", required = true) @RequestParam Integer periodNo,
            @Parameter(description = "预缴金额", required = true) @RequestParam BigDecimal amount) {
        taxService.prepayCIT(fiscalYear, periodNo, amount);
        return Result.success();
    }

    @Operation(summary = "汇算清缴企业所得税")
    @GetMapping("/cit/settle")
    public Result<Map<String, Object>> settleCIT(
            @Parameter(description = "会计年度", required = true) @RequestParam Integer fiscalYear) {
        Map<String, Object> result = taxService.settleCIT(fiscalYear);
        return Result.success(result);
    }

    @Operation(summary = "查询增值税记录")
    @GetMapping("/vat")
    public Result<FinanceVAT> getVATRecord(
            @Parameter(description = "会计年度", required = true) @RequestParam Integer fiscalYear,
            @Parameter(description = "期间", required = true) @RequestParam Integer periodNo) {
        FinanceVAT vat = taxService.getVATRecord(fiscalYear, periodNo);
        return Result.success(vat);
    }

    @Operation(summary = "查询企业所得税记录")
    @GetMapping("/cit")
    public Result<FinanceCIT> getCITRecord(
            @Parameter(description = "会计年度", required = true) @RequestParam Integer fiscalYear) {
        FinanceCIT cit = taxService.getCITRecord(fiscalYear);
        return Result.success(cit);
    }

    @Operation(summary = "查询个人所得税记录")
    @GetMapping("/pit")
    public Result<List<FinancePIT>> getPITRecords(
            @Parameter(description = "员工ID", required = true) @RequestParam Long employeeId,
            @Parameter(description = "会计年度", required = true) @RequestParam Integer fiscalYear) {
        List<FinancePIT> pitList = taxService.getPITRecords(employeeId, fiscalYear);
        return Result.success(pitList);
    }

    @Operation(summary = "根据发票计算增值税")
    @PostMapping("/vat/calculateFromInvoices")
    public Result<Map<String, BigDecimal>> calculateVATFromInvoices(@RequestBody List<Long> invoiceIds) {
        // TODO: 需要根据invoiceIds查询发票列表，然后计算
        return Result.success(Map.of());
    }
}

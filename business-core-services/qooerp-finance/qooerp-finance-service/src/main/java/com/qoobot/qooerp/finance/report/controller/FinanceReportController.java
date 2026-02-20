package com.qoobot.qooerp.finance.report.controller;

import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.finance.report.domain.FinanceBalanceSheet;
import com.qoobot.qooerp.finance.report.domain.FinanceIncomeStatement;
import com.qoobot.qooerp.finance.report.service.IFinanceReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 财务报表控制器
 *
 * @author QooERP Team
 * @since 2026-02-17
 */
@Tag(name = "财务报表管理", description = "财务报表相关接口")
@RestController
@RequestMapping("/finance/report")
@RequiredArgsConstructor
public class FinanceReportController {

    private final IFinanceReportService reportService;

    @Operation(summary = "生成资产负债表")
    @PostMapping("/balanceSheet/generate")
    public Result<FinanceBalanceSheet> generateBalanceSheet(
            @Parameter(description = "会计年度", required = true) @RequestParam Integer fiscalYear,
            @Parameter(description = "期间", required = true) @RequestParam Integer periodNo) {
        FinanceBalanceSheet balanceSheet = reportService.generateBalanceSheet(fiscalYear, periodNo);
        return Result.success(balanceSheet);
    }

    @Operation(summary = "生成利润表")
    @PostMapping("/incomeStatement/generate")
    public Result<FinanceIncomeStatement> generateIncomeStatement(
            @Parameter(description = "会计年度", required = true) @RequestParam Integer fiscalYear,
            @Parameter(description = "期间", required = true) @RequestParam Integer periodNo) {
        FinanceIncomeStatement incomeStatement = reportService.generateIncomeStatement(fiscalYear, periodNo);
        return Result.success(incomeStatement);
    }

    @Operation(summary = "生成现金流量表")
    @PostMapping("/cashFlow/generate")
    public Result<Map<String, Object>> generateCashFlowStatement(
            @Parameter(description = "会计年度", required = true) @RequestParam Integer fiscalYear,
            @Parameter(description = "期间", required = true) @RequestParam Integer periodNo) {
        Map<String, Object> cashFlow = reportService.generateCashFlowStatement(fiscalYear, periodNo);
        return Result.success(cashFlow);
    }

    @Operation(summary = "生成合并报表")
    @PostMapping("/consolidated/generate")
    public Result<Map<String, Object>> generateConsolidatedReport(
            @Parameter(description = "会计年度", required = true) @RequestParam Integer fiscalYear,
            @Parameter(description = "期间", required = true) @RequestParam Integer periodNo,
            @Parameter(description = "公司ID列表", required = true) @RequestBody List<Long> companyIds) {
        Map<String, Object> report = reportService.generateConsolidatedReport(fiscalYear, periodNo, companyIds);
        return Result.success(report);
    }

    @Operation(summary = "查询资产负债表")
    @GetMapping("/balanceSheet")
    public Result<FinanceBalanceSheet> getBalanceSheet(
            @Parameter(description = "会计年度", required = true) @RequestParam Integer fiscalYear,
            @Parameter(description = "期间", required = true) @RequestParam Integer periodNo) {
        FinanceBalanceSheet balanceSheet = reportService.getBalanceSheet(fiscalYear, periodNo);
        return Result.success(balanceSheet);
    }

    @Operation(summary = "查询利润表")
    @GetMapping("/incomeStatement")
    public Result<FinanceIncomeStatement> getIncomeStatement(
            @Parameter(description = "会计年度", required = true) @RequestParam Integer fiscalYear,
            @Parameter(description = "期间", required = true) @RequestParam Integer periodNo) {
        FinanceIncomeStatement incomeStatement = reportService.getIncomeStatement(fiscalYear, periodNo);
        return Result.success(incomeStatement);
    }

    @Operation(summary = "报表分析")
    @GetMapping("/analyze")
    public Result<Map<String, Object>> analyzeReports(
            @Parameter(description = "会计年度", required = true) @RequestParam Integer fiscalYear,
            @Parameter(description = "期间", required = true) @RequestParam Integer periodNo) {
        Map<String, Object> analysis = reportService.analyzeReports(fiscalYear, periodNo);
        return Result.success(analysis);
    }
}

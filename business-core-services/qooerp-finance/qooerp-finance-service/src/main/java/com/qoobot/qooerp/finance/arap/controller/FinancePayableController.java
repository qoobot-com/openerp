package com.qoobot.qooerp.finance.arap.controller;

import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.finance.arap.domain.FinancePayable;
import com.qoobot.qooerp.finance.arap.service.IFinancePayableService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 应付账款控制器
 *
 * @author QooERP Team
 * @since 2026-02-17
 */
@Tag(name = "应付账款管理", description = "应付账款相关接口")
@RestController
@RequestMapping("/finance/payable")
@RequiredArgsConstructor
public class FinancePayableController {

    private final IFinancePayableService payableService;

    @Operation(summary = "创建应付账款")
    @PostMapping("/create")
    public Result<FinancePayable> createPayable(@RequestBody FinancePayable payable) {
        FinancePayable created = payableService.createPayable(payable);
        return Result.success(created);
    }

    @Operation(summary = "取消应付账款")
    @Parameter(name = "payableId", description = "应付账款ID", required = true)
    @PostMapping("/cancel/{payableId}")
    public Result<Void> cancelPayable(@PathVariable Long payableId) {
        payableService.cancelPayable(payableId);
        return Result.success();
    }

    @Operation(summary = "付款核销")
    @PostMapping("/writeOff")
    public Result<Void> writeOffPayable(
            @Parameter(description = "应付账款ID", required = true) @RequestParam Long payableId,
            @Parameter(description = "付款单号", required = true) @RequestParam String paymentNo,
            @Parameter(description = "付款金额", required = true) @RequestParam BigDecimal amount) {
        payableService.writeOffPayable(payableId, paymentNo, amount);
        return Result.success();
    }

    @Operation(summary = "账龄分析")
    @GetMapping("/aging/analyze")
    public Result<Map<String, BigDecimal>> analyzeAging(
            @Parameter(description = "供应商名称") @RequestParam(required = false) String supplierName,
            @Parameter(description = "截止日期", required = true)
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam LocalDate asOfDate) {
        Map<String, BigDecimal> agingMap = payableService.analyzeAging(supplierName, asOfDate);
        return Result.success(agingMap);
    }

    @Operation(summary = "查询应付账款")
    @GetMapping("/query")
    public Result<List<FinancePayable>> queryPayables(
            @Parameter(description = "供应商名称") @RequestParam(required = false) String supplierName,
            @Parameter(description = "单据状态") @RequestParam(required = false) String status) {
        List<FinancePayable> payables = payableService.queryPayables(supplierName, status);
        return Result.success(payables);
    }

    @Operation(summary = "根据ID查询应付账款")
    @Parameter(name = "payableId", description = "应付账款ID", required = true)
    @GetMapping("/{payableId}")
    public Result<FinancePayable> getPayable(@PathVariable Long payableId) {
        FinancePayable payable = payableService.getById(payableId);
        return Result.success(payable);
    }

    @Operation(summary = "批量更新账龄")
    @PostMapping("/aging/batchUpdate")
    public Result<Void> batchUpdateAging(
            @Parameter(description = "截止日期", required = true)
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam LocalDate asOfDate) {
        payableService.batchUpdateAging(asOfDate);
        return Result.success();
    }

    @Operation(summary = "删除应付账款")
    @Parameter(name = "payableId", description = "应付账款ID", required = true)
    @DeleteMapping("/{payableId}")
    public Result<Void> deletePayable(@PathVariable Long payableId) {
        payableService.removeById(payableId);
        return Result.success();
    }
}

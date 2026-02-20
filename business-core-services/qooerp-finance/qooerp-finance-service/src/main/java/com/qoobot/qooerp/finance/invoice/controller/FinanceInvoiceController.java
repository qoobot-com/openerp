package com.qoobot.qooerp.finance.invoice.controller;

import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.finance.invoice.domain.FinanceInvoice;
import com.qoobot.qooerp.finance.invoice.service.IFinanceInvoiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 发票管理控制器
 * 
 * @author QooERP Team
 * @since 2026-02-17
 */
@Tag(name = "发票管理", description = "发票相关接口")
@RestController
@RequestMapping("/finance/invoice")
@RequiredArgsConstructor
public class FinanceInvoiceController {

    private final IFinanceInvoiceService invoiceService;

    @Operation(summary = "创建发票")
    @PostMapping("/create")
    public Result<FinanceInvoice> createInvoice(@RequestBody FinanceInvoice invoice) {
        return Result.success(invoiceService.createInvoice(invoice));
    }

    @Operation(summary = "认证进项发票")
    @PostMapping("/verify/{id}")
    public Result<Void> verifyInvoice(@Parameter(description = "发票ID") @PathVariable Long id) {
        invoiceService.verifyInvoice(id);
        return Result.success();
    }

    @Operation(summary = "作废发票")
    @PostMapping("/invalidate/{id}")
    public Result<Void> invalidateInvoice(@Parameter(description = "发票ID") @PathVariable Long id) {
        invoiceService.invalidateInvoice(id);
        return Result.success();
    }

    @Operation(summary = "查询客户发票")
    @GetMapping("/customer/{customerId}")
    public Result<List<FinanceInvoice>> getInvoicesByCustomer(
            @Parameter(description = "客户ID") @PathVariable Long customerId) {
        return Result.success(invoiceService.getInvoicesByCustomer(customerId));
    }

    @Operation(summary = "查询供应商发票")
    @GetMapping("/supplier/{supplierId}")
    public Result<List<FinanceInvoice>> getInvoicesBySupplier(
            @Parameter(description = "供应商ID") @PathVariable Long supplierId) {
        return Result.success(invoiceService.getInvoicesBySupplier(supplierId));
    }

    @Operation(summary = "查询日期范围内发票")
    @GetMapping("/dateRange")
    public Result<List<FinanceInvoice>> getInvoicesByDateRange(
            @Parameter(description = "开始日期") @RequestParam LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam LocalDate endDate) {
        return Result.success(invoiceService.getInvoicesByDateRange(startDate, endDate));
    }
}

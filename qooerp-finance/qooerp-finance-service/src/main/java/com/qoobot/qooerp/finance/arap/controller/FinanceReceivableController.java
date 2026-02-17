package com.qoobot.qooerp.finance.arap.controller;

import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.finance.arap.domain.FinanceReceivable;
import com.qoobot.qooerp.finance.arap.service.IFinanceReceivableService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 应收账款控制器
 * 
 * @author QooERP Team
 * @since 2026-02-17
 */
@Tag(name = "应收账款管理", description = "应收账款相关接口")
@RestController
@RequestMapping("/finance/ar/receivable")
@RequiredArgsConstructor
public class FinanceReceivableController {

    private final IFinanceReceivableService receivableService;

    @Operation(summary = "创建应收账款")
    @PostMapping("/create")
    public Result<FinanceReceivable> createReceivable(@RequestBody FinanceReceivable receivable) {
        return Result.success(receivableService.createReceivable(receivable));
    }

    @Operation(summary = "取消应收账款")
    @PostMapping("/cancel/{id}")
    public Result<Void> cancelReceivable(@Parameter(description = "应收账款ID") @PathVariable Long id) {
        receivableService.cancelReceivable(id);
        return Result.success();
    }

    @Operation(summary = "更新应收账款收款金额")
    @PostMapping("/updatePaidAmount")
    public Result<Void> updatePaidAmount(
            @Parameter(description = "应收账款ID") @RequestParam Long receivableId,
            @Parameter(description = "已收金额") @RequestParam BigDecimal paidAmount) {
        receivableService.updatePaidAmount(receivableId, paidAmount);
        return Result.success();
    }

    @Operation(summary = "查询客户应收账款")
    @GetMapping("/customer/{customerId}")
    public Result<List<FinanceReceivable>> getReceivablesByCustomer(
            @Parameter(description = "客户ID") @PathVariable Long customerId) {
        return Result.success(receivableService.getReceivablesByCustomer(customerId));
    }

    @Operation(summary = "客户账龄分析")
    @GetMapping("/aging/customer/{customerId}")
    public Result<Map<String, Object>> analyzeAgingByCustomer(
            @Parameter(description = "客户ID") @PathVariable Long customerId) {
        return Result.success(receivableService.analyzeAgingByCustomer(customerId));
    }

    @Operation(summary = "总体账龄分析")
    @GetMapping("/aging/overall")
    public Result<Map<String, Object>> analyzeAgingOverall() {
        return Result.success(receivableService.analyzeAgingOverall());
    }

    @Operation(summary = "查询逾期应收账款")
    @GetMapping("/overdue")
    public Result<List<FinanceReceivable>> getOverdueReceivables() {
        return Result.success(receivableService.getOverdueReceivables());
    }

    @Operation(summary = "查询客户未收金额")
    @GetMapping("/unpaid/customer/{customerId}")
    public Result<BigDecimal> getTotalUnpaidAmountByCustomer(
            @Parameter(description = "客户ID") @PathVariable Long customerId) {
        return Result.success(receivableService.getTotalUnpaidAmountByCustomer(customerId));
    }

    @Operation(summary = "查询总体未收金额")
    @GetMapping("/unpaid/total")
    public Result<BigDecimal> getTotalUnpaidAmountOverall() {
        return Result.success(receivableService.getTotalUnpaidAmountOverall());
    }
}

package com.qoobot.qooerp.finance.budget.controller;

import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.finance.budget.domain.FinanceBudget;
import com.qoobot.qooerp.finance.budget.service.IFinanceBudgetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 预算管理控制器
 *
 * @author QooERP Team
 * @since 2026-02-17
 */
@Tag(name = "预算管理", description = "预算相关接口")
@RestController
@RequestMapping("/finance/budget")
@RequiredArgsConstructor
public class FinanceBudgetController {

    private final IFinanceBudgetService budgetService;

    @Operation(summary = "创建预算")
    @PostMapping("/create")
    public Result<FinanceBudget> createBudget(@RequestBody FinanceBudget budget) {
        FinanceBudget created = budgetService.createBudget(budget);
        return Result.success(created);
    }

    @Operation(summary = "审批预算")
    @PostMapping("/approve")
    public Result<Void> approveBudget(
            @Parameter(description = "预算ID", required = true) @RequestParam Long budgetId,
            @Parameter(description = "是否通过", required = true) @RequestParam Boolean approved,
            @Parameter(description = "审批意见") @RequestParam(required = false) String comment) {
        budgetService.approveBudget(budgetId, approved, comment);
        return Result.success();
    }

    @Operation(summary = "预算控制检查")
    @GetMapping("/control/check")
    public Result<Boolean> checkBudgetControl(
            @Parameter(description = "预算ID", required = true) @RequestParam Long budgetId,
            @Parameter(description = "使用金额", required = true) @RequestParam BigDecimal amount) {
        boolean passed = budgetService.checkBudgetControl(budgetId, amount);
        return Result.success(passed);
    }

    @Operation(summary = "更新预算执行情况")
    @PostMapping("/execution/update")
    public Result<Void> updateBudgetExecution(
            @Parameter(description = "预算ID", required = true) @RequestParam Long budgetId,
            @Parameter(description = "实际金额", required = true) @RequestParam BigDecimal actualAmount) {
        budgetService.updateBudgetExecution(budgetId, actualAmount);
        return Result.success();
    }

    @Operation(summary = "查询预算执行情况")
    @GetMapping("/execution")
    public Result<Map<String, Object>> getBudgetExecution(
            @Parameter(description = "预算年度", required = true) @RequestParam Integer fiscalYear) {
        Map<String, Object> execution = budgetService.getBudgetExecution(fiscalYear, null);
        return Result.success(execution);
    }

    @Operation(summary = "查询预算列表")
    @GetMapping("/query")
    public Result<List<FinanceBudget>> queryBudgets(
            @Parameter(description = "预算年度") @RequestParam(required = false) Integer fiscalYear,
            @Parameter(description = "状态") @RequestParam(required = false) String status) {
        List<FinanceBudget> budgets = budgetService.queryBudgets(fiscalYear, null, status);
        return Result.success(budgets);
    }

    @Operation(summary = "查询超支预算")
    @GetMapping("/overbudget")
    public Result<List<FinanceBudget>> queryOverBudget(
            @Parameter(description = "会计年度", required = true) @RequestParam Integer fiscalYear) {
        List<FinanceBudget> budgets = budgetService.queryOverBudget(fiscalYear);
        return Result.success(budgets);
    }

    @Operation(summary = "调整预算")
    @PostMapping("/adjust")
    public Result<Void> adjustBudget(
            @Parameter(description = "预算ID", required = true) @RequestParam Long budgetId,
            @Parameter(description = "新预算金额", required = true) @RequestParam BigDecimal newBudgetAmount) {
        budgetService.adjustBudget(budgetId, newBudgetAmount);
        return Result.success();
    }

    @Operation(summary = "根据ID查询预算")
    @Parameter(name = "budgetId", description = "预算ID", required = true)
    @GetMapping("/{budgetId}")
    public Result<FinanceBudget> getBudget(@PathVariable Long budgetId) {
        FinanceBudget budget = budgetService.getById(budgetId);
        return Result.success(budget);
    }

    @Operation(summary = "删除预算")
    @Parameter(name = "budgetId", description = "预算ID", required = true)
    @DeleteMapping("/{budgetId}")
    public Result<Void> deleteBudget(@PathVariable Long budgetId) {
        budgetService.removeById(budgetId);
        return Result.success();
    }
}

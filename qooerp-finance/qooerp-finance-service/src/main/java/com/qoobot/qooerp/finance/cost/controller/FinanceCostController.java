package com.qoobot.qooerp.finance.cost.controller;

import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.finance.cost.domain.FinanceCostCenter;
import com.qoobot.qooerp.finance.cost.service.IFinanceCostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 成本管理控制器
 *
 * @author QooERP Team
 * @since 2026-02-17
 */
@Tag(name = "成本管理", description = "成本相关接口")
@RestController
@RequestMapping("/finance/cost")
@RequiredArgsConstructor
public class FinanceCostController {

    private final IFinanceCostService costService;

    @Operation(summary = "创建成本中心")
    @PostMapping("/center/create")
    public Result<FinanceCostCenter> createCostCenter(@RequestBody FinanceCostCenter costCenter) {
        FinanceCostCenter created = costService.createCostCenter(costCenter);
        return Result.success(created);
    }

    @Operation(summary = "成本分摊")
    @PostMapping("/allocate")
    public Result<Void> allocateCost(
            @Parameter(description = "源成本中心ID", required = true) @RequestParam Long sourceCenterId,
            @Parameter(description = "分摊金额", required = true) @RequestParam BigDecimal amount,
            @Parameter(description = "期间ID", required = true) @RequestParam Long periodId,
            @Parameter(description = "目标成本中心ID列表", required = true) @RequestBody List<Long> targetCenterIds) {
        costService.allocateCost(sourceCenterId, targetCenterIds, amount, periodId);
        return Result.success();
    }

    @Operation(summary = "计算成本差异")
    @GetMapping("/variance")
    public Result<Map<String, Object>> calculateCostVariance(
            @Parameter(description = "成本中心ID", required = true) @RequestParam Long costCenterId,
            @Parameter(description = "期间ID", required = true) @RequestParam Long periodId) {
        Map<String, Object> variance = costService.calculateCostVariance(costCenterId, periodId);
        return Result.success(variance);
    }

    @Operation(summary = "查询成本中心列表")
    @GetMapping("/center/query")
    public Result<List<FinanceCostCenter>> queryCostCenters(
            @Parameter(description = "部门ID") @RequestParam(required = false) Long deptId,
            @Parameter(description = "状态") @RequestParam(required = false) String status) {
        List<FinanceCostCenter> costCenters = costService.queryCostCenters(deptId, status);
        return Result.success(costCenters);
    }

    @Operation(summary = "成本分析")
    @GetMapping("/analyze")
    public Result<Map<String, Object>> analyzeCost(
            @Parameter(description = "会计年度", required = true) @RequestParam Integer fiscalYear,
            @Parameter(description = "成本中心ID") @RequestParam(required = false) Long costCenterId) {
        Map<String, Object> analysis = costService.analyzeCost(fiscalYear, costCenterId);
        return Result.success(analysis);
    }

    @Operation(summary = "成本归集")
    @PostMapping("/collect")
    public Result<Void> collectCost(
            @Parameter(description = "成本中心ID", required = true) @RequestParam Long costCenterId,
            @Parameter(description = "成本金额", required = true) @RequestParam BigDecimal amount) {
        costService.collectCost(costCenterId, amount, null);
        return Result.success();
    }

    @Operation(summary = "查询成本汇总")
    @GetMapping("/summary")
    public Result<Map<String, BigDecimal>> getCostSummary(
            @Parameter(description = "会计年度", required = true) @RequestParam Integer fiscalYear,
            @Parameter(description = "期间ID") @RequestParam(required = false) Long periodId) {
        Map<String, BigDecimal> summary = costService.getCostSummary(fiscalYear, periodId);
        return Result.success(summary);
    }

    @Operation(summary = "根据ID查询成本中心")
    @Parameter(name = "costCenterId", description = "成本中心ID", required = true)
    @GetMapping("/center/{costCenterId}")
    public Result<FinanceCostCenter> getCostCenter(@PathVariable Long costCenterId) {
        FinanceCostCenter costCenter = costService.getById(costCenterId);
        return Result.success(costCenter);
    }

    @Operation(summary = "删除成本中心")
    @Parameter(name = "costCenterId", description = "成本中心ID", required = true)
    @DeleteMapping("/center/{costCenterId}")
    public Result<Void> deleteCostCenter(@PathVariable Long costCenterId) {
        costService.removeById(costCenterId);
        return Result.success();
    }
}

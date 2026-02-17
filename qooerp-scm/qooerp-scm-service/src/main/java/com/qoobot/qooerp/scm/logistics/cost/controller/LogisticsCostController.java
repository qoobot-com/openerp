package com.qoobot.qooerp.scm.logistics.cost.controller;

import com.qoobot.qooerp.common.response.PageResult;
import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.scm.logistics.cost.domain.LogisticsCost;
import com.qoobot.qooerp.scm.logistics.cost.dto.LogisticsCostDTO;
import com.qoobot.qooerp.scm.logistics.cost.service.ILogisticsCostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 物流费用控制器
 *
 * @author QooERP
 * @since 2026-02-17
 */
@Tag(name = "物流费用管理", description = "物流费用相关接口")
@RestController
@RequestMapping("/api/scm/logistics/costs")
@RequiredArgsConstructor
public class LogisticsCostController {

    private final ILogisticsCostService logisticsCostService;

    /**
     * 创建物流费用
     */
    @Operation(summary = "创建物流费用")
    @PostMapping
    public Result<Long> create(@RequestBody LogisticsCostDTO dto) {
        Long id = logisticsCostService.create(dto);
        return Result.success(id);
    }

    /**
     * 更新物流费用
     */
    @Operation(summary = "更新物流费用")
    @PutMapping("/{id}")
    public Result<Boolean> update(@PathVariable Long id, @RequestBody LogisticsCostDTO dto) {
        dto.setId(id);
        boolean success = logisticsCostService.update(dto);
        return Result.success(success);
    }

    /**
     * 删除物流费用
     */
    @Operation(summary = "删除物流费用")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        boolean success = logisticsCostService.delete(id);
        return Result.success(success);
    }

    /**
     * 获取费用详情
     */
    @Operation(summary = "获取费用详情")
    @GetMapping("/{id}")
    public Result<LogisticsCostDTO> getDetail(@PathVariable Long id) {
        LogisticsCostDTO dto = logisticsCostService.getDetail(id);
        return Result.success(dto);
    }

    /**
     * 分页查询费用列表
     */
    @Operation(summary = "分页查询费用列表")
    @GetMapping
    public Result<PageResult<LogisticsCost>> queryPage(@RequestParam(required = false) Long trackingId,
                                                        @RequestParam(defaultValue = "1") Integer pageNo,
                                                        @RequestParam(defaultValue = "10") Integer pageSize) {
        PageResult<LogisticsCost> pageResult = logisticsCostService.queryPage(trackingId, pageNo, pageSize);
        return Result.success(pageResult);
    }

    /**
     * 根据物流跟踪ID获取费用列表
     */
    @Operation(summary = "根据物流跟踪ID获取费用列表")
    @GetMapping("/tracking/{trackingId}")
    public Result<List<LogisticsCost>> getCostsByTrackingId(@PathVariable Long trackingId) {
        List<LogisticsCost> costs = logisticsCostService.getCostsByTrackingId(trackingId);
        return Result.success(costs);
    }

    /**
     * 根据订单ID获取费用列表
     */
    @Operation(summary = "根据订单ID获取费用列表")
    @GetMapping("/order/{orderId}")
    public Result<List<LogisticsCost>> getCostsByOrderId(@PathVariable Long orderId) {
        List<LogisticsCost> costs = logisticsCostService.getCostsByOrderId(orderId);
        return Result.success(costs);
    }

    /**
     * 计算物流跟踪总费用
     */
    @Operation(summary = "计算物流跟踪总费用")
    @GetMapping("/tracking/{trackingId}/total")
    public Result<BigDecimal> calculateTotalCost(@PathVariable Long trackingId) {
        BigDecimal totalCost = logisticsCostService.calculateTotalCost(trackingId);
        return Result.success(totalCost);
    }

    /**
     * 更新付款状态
     */
    @Operation(summary = "更新付款状态")
    @PutMapping("/{id}/payment-status")
    public Result<Boolean> updatePaymentStatus(@PathVariable Long id,
                                               @RequestParam String paidStatus,
                                               @RequestParam(required = false) BigDecimal paidAmount,
                                               @RequestParam(required = false) LocalDate paidDate) {
        boolean success = logisticsCostService.updatePaymentStatus(id, paidStatus, paidAmount, paidDate);
        return Result.success(success);
    }

    /**
     * 获取费用统计
     */
    @Operation(summary = "获取费用统计")
    @GetMapping("/tracking/{trackingId}/statistics")
    public Result<Map<String, BigDecimal>> getCostStatistics(@PathVariable Long trackingId) {
        Map<String, BigDecimal> statistics = logisticsCostService.getCostStatistics(trackingId);
        return Result.success(statistics);
    }
}

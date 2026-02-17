package com.qoobot.qooerp.production.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.qoobot.qooerp.common.result.Result;
import com.qoobot.qooerp.production.dto.ProductionOrderDTO;
import com.qoobot.qooerp.production.service.ProductionOrderService;
import com.qoobot.qooerp.production.vo.ProductionOrderVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * 生产订单Controller
 */
@Tag(name = "生产订单管理", description = "生产订单相关接口")
@RestController
@RequestMapping("/production/order")
@RequiredArgsConstructor
public class ProductionOrderController {

    private final ProductionOrderService productionOrderService;

    @Operation(summary = "创建生产订单")
    @PostMapping("/create")
    public Result<Long> createProductionOrder(@Valid @RequestBody ProductionOrderDTO dto) {
        Long id = productionOrderService.createProductionOrder(dto);
        return Result.success(id);
    }

    @Operation(summary = "更新生产订单")
    @PutMapping("/update/{id}")
    public Result<Boolean> updateProductionOrder(
            @Parameter(description = "生产订单ID") @PathVariable Long id,
            @Valid @RequestBody ProductionOrderDTO dto) {
        Boolean result = productionOrderService.updateProductionOrder(id, dto);
        return Result.success(result);
    }

    @Operation(summary = "删除生产订单")
    @DeleteMapping("/delete/{id}")
    public Result<Boolean> deleteProductionOrder(@Parameter(description = "生产订单ID") @PathVariable Long id) {
        Boolean result = productionOrderService.deleteProductionOrder(id);
        return Result.success(result);
    }

    @Operation(summary = "批量删除生产订单")
    @DeleteMapping("/batch-delete")
    public Result<Boolean> batchDeleteProductionOrder(@RequestBody List<Long> ids) {
        Boolean result = productionOrderService.batchDeleteProductionOrder(ids);
        return Result.success(result);
    }

    @Operation(summary = "根据ID查询生产订单")
    @GetMapping("/get/{id}")
    public Result<ProductionOrderVO> getProductionOrderById(@Parameter(description = "生产订单ID") @PathVariable Long id) {
        ProductionOrderVO vo = productionOrderService.getProductionOrderById(id);
        return Result.success(vo);
    }

    @Operation(summary = "分页查询生产订单")
    @GetMapping("/page")
    public Result<IPage<ProductionOrderVO>> queryProductionOrderPage(
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "订单编号") @RequestParam(required = false) String orderNo,
            @Parameter(description = "状态") @RequestParam(required = false) String status,
            @Parameter(description = "车间ID") @RequestParam(required = false) Long workshopId) {
        IPage<ProductionOrderVO> page = productionOrderService.queryProductionOrderPage(current, size, orderNo, status, workshopId);
        return Result.success(page);
    }

    @Operation(summary = "开始生产")
    @PostMapping("/start/{id}")
    public Result<Boolean> startProduction(@Parameter(description = "生产订单ID") @PathVariable Long id) {
        Boolean result = productionOrderService.startProduction(id);
        return Result.success(result);
    }

    @Operation(summary = "完成生产")
    @PostMapping("/complete/{id}")
    public Result<Boolean> completeProduction(@Parameter(description = "生产订单ID") @PathVariable Long id) {
        Boolean result = productionOrderService.completeProduction(id);
        return Result.success(result);
    }

    @Operation(summary = "取消生产订单")
    @PostMapping("/cancel/{id}")
    public Result<Boolean> cancelProductionOrder(@Parameter(description = "生产订单ID") @PathVariable Long id) {
        Boolean result = productionOrderService.cancelProductionOrder(id);
        return Result.success(result);
    }

    @Operation(summary = "更新订单进度")
    @PostMapping("/update-progress/{id}")
    public Result<Boolean> updateProgress(
            @Parameter(description = "生产订单ID") @PathVariable Long id,
            @Parameter(description = "已完成数量") @RequestParam BigDecimal completedQuantity,
            @Parameter(description = "合格数量") @RequestParam BigDecimal qualifiedQuantity,
            @Parameter(description = "不良数量") @RequestParam BigDecimal rejectQuantity) {
        Boolean result = productionOrderService.updateProgress(id, completedQuantity, qualifiedQuantity, rejectQuantity);
        return Result.success(result);
    }
}

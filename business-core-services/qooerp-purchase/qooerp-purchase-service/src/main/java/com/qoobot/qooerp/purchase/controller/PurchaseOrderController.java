package com.qoobot.qooerp.purchase.controller;

import com.qoobot.qooerp.common.response.PageResult;
import com.qoobot.qooerp.common.result.Result;
import com.qoobot.qooerp.purchase.dto.OrderCreateDTO;
import com.qoobot.qooerp.purchase.dto.OrderDTO;
import com.qoobot.qooerp.purchase.dto.OrderQueryDTO;
import com.qoobot.qooerp.purchase.dto.OrderUpdateDTO;
import com.qoobot.qooerp.purchase.dto.RejectReasonDTO;
import com.qoobot.qooerp.purchase.service.PurchaseOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "采购订单管理", description = "采购订单相关接口")
@RestController
@RequestMapping("/api/purchase/orders")
@RequiredArgsConstructor
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;

    @Operation(summary = "创建采购订单")
    @PostMapping
    public Result<Long> createOrder(@Valid @RequestBody OrderCreateDTO dto) {
        return purchaseOrderService.createOrder(dto);
    }

    @Operation(summary = "更新采购订单")
    @PutMapping("/{id}")
    public Result<Void> updateOrder(@PathVariable Long id, @Valid @RequestBody OrderUpdateDTO dto) {
        dto.setId(id);
        return purchaseOrderService.updateOrder(dto);
    }

    @Operation(summary = "删除采购订单")
    @DeleteMapping("/{id}")
    public Result<Void> deleteOrder(@PathVariable Long id) {
        return purchaseOrderService.deleteOrder(id);
    }

    @Operation(summary = "提交审核")
    @PostMapping("/{id}/submit")
    public Result<Void> submitOrder(@PathVariable Long id) {
        return purchaseOrderService.submitOrder(id);
    }

    @Operation(summary = "审核订单")
    @PostMapping("/{id}/approve")
    public Result<Void> approveOrder(@PathVariable Long id) {
        return purchaseOrderService.approveOrder(id);
    }

    @Operation(summary = "驳回订单")
    @PostMapping("/{id}/reject")
    public Result<Void> rejectOrder(@PathVariable Long id, @Valid @RequestBody RejectReasonDTO dto) {
        return purchaseOrderService.rejectOrder(id, dto);
    }

    @Operation(summary = "取消订单")
    @PostMapping("/{id}/cancel")
    public Result<Void> cancelOrder(@PathVariable Long id) {
        return purchaseOrderService.cancelOrder(id);
    }

    @Operation(summary = "完成订单")
    @PostMapping("/{id}/complete")
    public Result<Void> completeOrder(@PathVariable Long id) {
        return purchaseOrderService.completeOrder(id);
    }

    @Operation(summary = "获取订单详情")
    @GetMapping("/{id}")
    public Result<OrderDTO> getOrder(@PathVariable Long id) {
        return purchaseOrderService.getOrder(id);
    }

    @Operation(summary = "分页查询订单")
    @GetMapping
    public Result<PageResult<OrderDTO>> queryPage(OrderQueryDTO dto) {
        return purchaseOrderService.queryPage(dto);
    }
}

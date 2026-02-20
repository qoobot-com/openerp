package com.qoobot.qooerp.sales.order.controller;

import com.qoobot.qooerp.common.response.PageResult;
import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.sales.order.dto.OrderDTO;
import com.qoobot.qooerp.sales.order.dto.OrderQueryDTO;
import com.qoobot.qooerp.sales.order.service.SalesOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 销售订单控制器
 */
@Tag(name = "销售订单管理", description = "销售订单相关接口")
@RestController
@RequestMapping("/api/sales/orders")
@RequiredArgsConstructor
public class SalesOrderController {

    private final SalesOrderService orderService;

    @Operation(summary = "创建订单")
    @PostMapping
    public Result<Long> createOrder(@RequestBody OrderDTO orderDTO) {
        return orderService.createOrder(orderDTO);
    }

    @Operation(summary = "查询订单")
    @GetMapping("/{id}")
    public Result<OrderDTO> getOrder(@PathVariable Long id) {
        return orderService.getOrder(id);
    }

    @Operation(summary = "分页查询订单")
    @GetMapping
    public Result<PageResult<OrderDTO>> queryOrders(OrderQueryDTO queryDTO) {
        return orderService.queryOrders(queryDTO);
    }

    @Operation(summary = "审核订单")
    @PostMapping("/{id}/approve")
    public Result<Void> approveOrder(@PathVariable Long id, @RequestParam Long approverId) {
        return orderService.approveOrder(id, approverId);
    }

    @Operation(summary = "取消订单")
    @PostMapping("/{id}/cancel")
    public Result<Void> cancelOrder(@PathVariable Long id, @RequestParam String reason) {
        return orderService.cancelOrder(id, reason);
    }

    @Operation(summary = "订单发货")
    @PostMapping("/{id}/ship")
    public Result<Void> shipOrder(@PathVariable Long id) {
        return orderService.shipOrder(id);
    }
}

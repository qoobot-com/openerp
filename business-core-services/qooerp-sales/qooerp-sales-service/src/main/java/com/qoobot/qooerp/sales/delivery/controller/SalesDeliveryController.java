package com.qoobot.qooerp.sales.delivery.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.common.result.Result;
import com.qoobot.qooerp.sales.delivery.dto.DeliveryDTO;
import com.qoobot.qooerp.sales.delivery.service.SalesDeliveryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 销售发货 Controller
 */
@Tag(name = "销售发货管理", description = "销售发货相关接口")
@RestController
@RequestMapping("/api/sales/deliveries")
@RequiredArgsConstructor
public class SalesDeliveryController {

    private final SalesDeliveryService deliveryService;

    @Operation(summary = "创建发货单")
    @PostMapping
    public Result<DeliveryDTO> createDelivery(@RequestBody DeliveryDTO dto) {
        return Result.success(deliveryService.createDelivery(dto));
    }

    @Operation(summary = "更新发货单")
    @PutMapping("/{id}")
    public Result<DeliveryDTO> updateDelivery(@PathVariable Long id, @RequestBody DeliveryDTO dto) {
        return Result.success(deliveryService.updateDelivery(id, dto));
    }

    @Operation(summary = "删除发货单")
    @DeleteMapping("/{id}")
    public Result<Void> deleteDelivery(@PathVariable Long id) {
        deliveryService.deleteDelivery(id);
        return Result.success();
    }

    @Operation(summary = "查询发货单")
    @GetMapping("/{id}")
    public Result<DeliveryDTO> getDeliveryById(@PathVariable Long id) {
        return Result.success(deliveryService.getDeliveryById(id));
    }

    @Operation(summary = "分页查询发货单")
    @GetMapping
    public Result<Page<DeliveryDTO>> queryDeliveries(
            @RequestParam(required = false) Long orderId,
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(deliveryService.queryDeliveries(orderId, current, size));
    }

    @Operation(summary = "根据订单ID查询发货单")
    @GetMapping("/order/{orderId}")
    public Result<List<DeliveryDTO>> getDeliveriesByOrderId(@PathVariable Long orderId) {
        return Result.success(deliveryService.getDeliveriesByOrderId(orderId));
    }

    @Operation(summary = "确认发货")
    @PostMapping("/{id}/confirm")
    public Result<Void> confirmDelivery(@PathVariable Long id) {
        deliveryService.confirmDelivery(id);
        return Result.success();
    }

    @Operation(summary = "确认签收")
    @PostMapping("/{id}/receive")
    public Result<Void> confirmReceive(@PathVariable Long id, @RequestParam String receiver) {
        deliveryService.confirmReceive(id, receiver);
        return Result.success();
    }
}

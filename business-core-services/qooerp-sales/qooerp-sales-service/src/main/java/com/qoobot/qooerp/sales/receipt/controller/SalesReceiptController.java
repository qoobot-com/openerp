package com.qoobot.qooerp.sales.receipt.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.common.result.Result;
import com.qoobot.qooerp.sales.receipt.dto.ReceiptDTO;
import com.qoobot.qooerp.sales.receipt.service.SalesReceiptService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * 销售收款 Controller
 */
@Tag(name = "销售收款管理", description = "销售收款相关接口")
@RestController
@RequestMapping("/api/sales/receipts")
@RequiredArgsConstructor
public class SalesReceiptController {

    private final SalesReceiptService receiptService;

    @Operation(summary = "创建收款单")
    @PostMapping
    public Result<ReceiptDTO> createReceipt(@RequestBody ReceiptDTO dto) {
        return Result.success(receiptService.createReceipt(dto));
    }

    @Operation(summary = "更新收款单")
    @PutMapping("/{id}")
    public Result<ReceiptDTO> updateReceipt(@PathVariable Long id, @RequestBody ReceiptDTO dto) {
        return Result.success(receiptService.updateReceipt(id, dto));
    }

    @Operation(summary = "删除收款单")
    @DeleteMapping("/{id}")
    public Result<Void> deleteReceipt(@PathVariable Long id) {
        receiptService.deleteReceipt(id);
        return Result.success();
    }

    @Operation(summary = "查询收款单")
    @GetMapping("/{id}")
    public Result<ReceiptDTO> getReceiptById(@PathVariable Long id) {
        return Result.success(receiptService.getReceiptById(id));
    }

    @Operation(summary = "分页查询收款单")
    @GetMapping
    public Result<Page<ReceiptDTO>> queryReceipts(
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(receiptService.queryReceipts(customerId, status, current, size));
    }

    @Operation(summary = "根据订单ID查询收款单")
    @GetMapping("/order/{orderId}")
    public Result<List<ReceiptDTO>> getReceiptsByOrderId(@PathVariable Long orderId) {
        return Result.success(receiptService.getReceiptsByOrderId(orderId));
    }

    @Operation(summary = "确认收款")
    @PostMapping("/{id}/confirm")
    public Result<Void> confirmReceipt(@PathVariable Long id, @RequestParam Long receiverId) {
        receiptService.confirmReceipt(id, receiverId);
        return Result.success();
    }

    @Operation(summary = "核销应收账款")
    @PostMapping("/{id}/writeoff")
    public Result<Void> writeoffReceipt(
            @PathVariable Long id,
            @RequestParam Long orderId,
            @RequestParam BigDecimal amount) {
        receiptService.writeoffReceipt(id, orderId, amount);
        return Result.success();
    }
}

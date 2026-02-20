package com.qoobot.qooerp.purchase.controller;

import com.qoobot.qooerp.common.response.PageResult;
import com.qoobot.qooerp.common.result.Result;
import com.qoobot.qooerp.purchase.dto.ReceiptCreateDTO;
import com.qoobot.qooerp.purchase.dto.ReceiptDTO;
import com.qoobot.qooerp.purchase.dto.ReceiptQueryDTO;
import com.qoobot.qooerp.purchase.service.PurchaseReceiptService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "采购入库管理", description = "采购入库相关接口")
@RestController
@RequestMapping("/api/purchase/receipts")
@RequiredArgsConstructor
public class PurchaseReceiptController {

    private final PurchaseReceiptService purchaseReceiptService;

    @Operation(summary = "创建入库单")
    @PostMapping
    public Result<Long> createReceipt(@Valid @RequestBody ReceiptCreateDTO dto) {
        return purchaseReceiptService.createReceipt(dto);
    }

    @Operation(summary = "删除入库单")
    @DeleteMapping("/{id}")
    public Result<Void> deleteReceipt(@PathVariable Long id) {
        return purchaseReceiptService.deleteReceipt(id);
    }

    @Operation(summary = "审核入库单")
    @PostMapping("/{id}/approve")
    public Result<Void> approveReceipt(@PathVariable Long id) {
        return purchaseReceiptService.approveReceipt(id);
    }

    @Operation(summary = "取消入库单")
    @PostMapping("/{id}/cancel")
    public Result<Void> cancelReceipt(@PathVariable Long id) {
        return purchaseReceiptService.cancelReceipt(id);
    }

    @Operation(summary = "获取入库单详情")
    @GetMapping("/{id}")
    public Result<ReceiptDTO> getReceipt(@PathVariable Long id) {
        return purchaseReceiptService.getReceipt(id);
    }

    @Operation(summary = "分页查询入库单")
    @GetMapping
    public Result<PageResult<ReceiptDTO>> queryPage(ReceiptQueryDTO dto) {
        return purchaseReceiptService.queryPage(dto);
    }
}

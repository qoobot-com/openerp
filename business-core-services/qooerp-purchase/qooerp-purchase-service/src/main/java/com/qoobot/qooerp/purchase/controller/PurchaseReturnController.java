package com.qoobot.qooerp.purchase.controller;

import com.qoobot.qooerp.common.response.PageResult;
import com.qoobot.qooerp.common.result.Result;
import com.qoobot.qooerp.purchase.dto.ReturnCreateDTO;
import com.qoobot.qooerp.purchase.dto.ReturnDTO;
import com.qoobot.qooerp.purchase.dto.ReturnQueryDTO;
import com.qoobot.qooerp.purchase.service.PurchaseReturnService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "采购退货管理", description = "采购退货相关接口")
@RestController
@RequestMapping("/api/purchase/returns")
@RequiredArgsConstructor
public class PurchaseReturnController {

    private final PurchaseReturnService purchaseReturnService;

    @Operation(summary = "创建退货单")
    @PostMapping
    public Result<Long> createReturn(@Valid @RequestBody ReturnCreateDTO dto) {
        return purchaseReturnService.createReturn(dto);
    }

    @Operation(summary = "删除退货单")
    @DeleteMapping("/{id}")
    public Result<Void> deleteReturn(@PathVariable Long id) {
        return purchaseReturnService.deleteReturn(id);
    }

    @Operation(summary = "审核退货单")
    @PostMapping("/{id}/approve")
    public Result<Void> approveReturn(@PathVariable Long id) {
        return purchaseReturnService.approveReturn(id);
    }

    @Operation(summary = "完成退货")
    @PostMapping("/{id}/complete")
    public Result<Void> completeReturn(@PathVariable Long id) {
        return purchaseReturnService.completeReturn(id);
    }

    @Operation(summary = "取消退货单")
    @PostMapping("/{id}/cancel")
    public Result<Void> cancelReturn(@PathVariable Long id) {
        return purchaseReturnService.cancelReturn(id);
    }

    @Operation(summary = "获取退货单详情")
    @GetMapping("/{id}")
    public Result<ReturnDTO> getReturn(@PathVariable Long id) {
        return purchaseReturnService.getReturn(id);
    }

    @Operation(summary = "分页查询退货单")
    @GetMapping
    public Result<PageResult<ReturnDTO>> queryPage(ReturnQueryDTO dto) {
        return purchaseReturnService.queryPage(dto);
    }
}

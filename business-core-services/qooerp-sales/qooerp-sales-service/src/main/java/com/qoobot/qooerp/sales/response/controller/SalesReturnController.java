package com.qoobot.qooerp.sales.response.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.common.result.Result;
import com.qoobot.qooerp.sales.response.dto.ReturnDTO;
import com.qoobot.qooerp.sales.response.service.SalesReturnService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 销售退货 Controller
 */
@Tag(name = "销售退货管理", description = "销售退货相关接口")
@RestController
@RequestMapping("/api/sales/returns")
@RequiredArgsConstructor
public class SalesReturnController {

    private final SalesReturnService returnService;

    @Operation(summary = "创建退货单")
    @PostMapping
    public Result<ReturnDTO> createReturn(@RequestBody ReturnDTO dto) {
        return Result.success(returnService.createReturn(dto));
    }

    @Operation(summary = "更新退货单")
    @PutMapping("/{id}")
    public Result<ReturnDTO> updateReturn(@PathVariable Long id, @RequestBody ReturnDTO dto) {
        return Result.success(returnService.updateReturn(id, dto));
    }

    @Operation(summary = "删除退货单")
    @DeleteMapping("/{id}")
    public Result<Void> deleteReturn(@PathVariable Long id) {
        returnService.deleteReturn(id);
        return Result.success();
    }

    @Operation(summary = "查询退货单")
    @GetMapping("/{id}")
    public Result<ReturnDTO> getReturnById(@PathVariable Long id) {
        return Result.success(returnService.getReturnById(id));
    }

    @Operation(summary = "分页查询退货单")
    @GetMapping
    public Result<Page<ReturnDTO>> queryReturns(
            @RequestParam(required = false) Long orderId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(returnService.queryReturns(orderId, status, current, size));
    }

    @Operation(summary = "审核退货单")
    @PostMapping("/{id}/approve")
    public Result<Void> approveReturn(
            @PathVariable Long id,
            @RequestParam Long approverId,
            @RequestParam String approveRemark,
            @RequestParam boolean approved) {
        returnService.approveReturn(id, approverId, approveRemark, approved);
        return Result.success();
    }

    @Operation(summary = "处理退货")
    @PostMapping("/{id}/process")
    public Result<Void> processReturn(@PathVariable Long id) {
        returnService.processReturn(id);
        return Result.success();
    }
}

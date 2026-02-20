package com.qoobot.qooerp.production.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.qoobot.qooerp.common.result.Result;
import com.qoobot.qooerp.production.dto.ProductionReceiptDTO;
import com.qoobot.qooerp.production.service.ProductionReceiptService;
import com.qoobot.qooerp.production.vo.ProductionReceiptVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "生产入库管理", description = "生产入库相关接口")
@RestController
@RequestMapping("/production/receipt")
@RequiredArgsConstructor
public class ProductionReceiptController {

    private final ProductionReceiptService productionReceiptService;

    @Operation(summary = "创建生产入库")
    @PostMapping("/create")
    public Result<Long> createProductionReceipt(@Valid @RequestBody ProductionReceiptDTO dto) {
        Long id = productionReceiptService.createProductionReceipt(dto);
        return Result.success(id);
    }

    @Operation(summary = "更新生产入库")
    @PutMapping("/update/{id}")
    public Result<Boolean> updateProductionReceipt(@PathVariable Long id, @Valid @RequestBody ProductionReceiptDTO dto) {
        Boolean result = productionReceiptService.updateProductionReceipt(id, dto);
        return Result.success(result);
    }

    @Operation(summary = "删除生产入库")
    @DeleteMapping("/delete/{id}")
    public Result<Boolean> deleteProductionReceipt(@PathVariable Long id) {
        Boolean result = productionReceiptService.deleteProductionReceipt(id);
        return Result.success(result);
    }

    @Operation(summary = "批量删除生产入库")
    @DeleteMapping("/batch-delete")
    public Result<Boolean> batchDeleteProductionReceipt(@RequestBody List<Long> ids) {
        Boolean result = productionReceiptService.batchDeleteProductionReceipt(ids);
        return Result.success(result);
    }

    @Operation(summary = "根据ID查询生产入库")
    @GetMapping("/get/{id}")
    public Result<ProductionReceiptVO> getProductionReceiptById(@PathVariable Long id) {
        ProductionReceiptVO vo = productionReceiptService.getProductionReceiptById(id);
        return Result.success(vo);
    }

    @Operation(summary = "分页查询生产入库")
    @GetMapping("/page")
    public Result<IPage<ProductionReceiptVO>> queryProductionReceiptPage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String receiptNo,
            @RequestParam(required = false) String status) {
        IPage<ProductionReceiptVO> page = productionReceiptService.queryProductionReceiptPage(current, size, receiptNo, status);
        return Result.success(page);
    }

    @Operation(summary = "批准生产入库")
    @PostMapping("/approve/{id}")
    public Result<Boolean> approveProductionReceipt(@PathVariable Long id) {
        Boolean result = productionReceiptService.approveProductionReceipt(id);
        return Result.success(result);
    }

    @Operation(summary = "生产入库")
    @PostMapping("/warehouse/{id}")
    public Result<Boolean> warehouseReceipt(@PathVariable Long id) {
        Boolean result = productionReceiptService.warehouseReceipt(id);
        return Result.success(result);
    }
}

package com.qoobot.qooerp.production.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.qoobot.qooerp.common.result.Result;
import com.qoobot.qooerp.production.dto.ProductionMaterialDTO;
import com.qoobot.qooerp.production.service.ProductionMaterialService;
import com.qoobot.qooerp.production.vo.ProductionMaterialVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "生产领料管理", description = "生产领料相关接口")
@RestController
@RequestMapping("/production/material")
@RequiredArgsConstructor
public class ProductionMaterialController {

    private final ProductionMaterialService productionMaterialService;

    @Operation(summary = "创建生产领料")
    @PostMapping("/create")
    public Result<Long> createProductionMaterial(@Valid @RequestBody ProductionMaterialDTO dto) {
        Long id = productionMaterialService.createProductionMaterial(dto);
        return Result.success(id);
    }

    @Operation(summary = "更新生产领料")
    @PutMapping("/update/{id}")
    public Result<Boolean> updateProductionMaterial(@PathVariable Long id, @Valid @RequestBody ProductionMaterialDTO dto) {
        Boolean result = productionMaterialService.updateProductionMaterial(id, dto);
        return Result.success(result);
    }

    @Operation(summary = "删除生产领料")
    @DeleteMapping("/delete/{id}")
    public Result<Boolean> deleteProductionMaterial(@PathVariable Long id) {
        Boolean result = productionMaterialService.deleteProductionMaterial(id);
        return Result.success(result);
    }

    @Operation(summary = "批量删除生产领料")
    @DeleteMapping("/batch-delete")
    public Result<Boolean> batchDeleteProductionMaterial(@RequestBody List<Long> ids) {
        Boolean result = productionMaterialService.batchDeleteProductionMaterial(ids);
        return Result.success(result);
    }

    @Operation(summary = "根据ID查询生产领料")
    @GetMapping("/get/{id}")
    public Result<ProductionMaterialVO> getProductionMaterialById(@PathVariable Long id) {
        ProductionMaterialVO vo = productionMaterialService.getProductionMaterialById(id);
        return Result.success(vo);
    }

    @Operation(summary = "分页查询生产领料")
    @GetMapping("/page")
    public Result<IPage<ProductionMaterialVO>> queryProductionMaterialPage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String materialNo,
            @RequestParam(required = false) String status) {
        IPage<ProductionMaterialVO> page = productionMaterialService.queryProductionMaterialPage(current, size, materialNo, status);
        return Result.success(page);
    }

    @Operation(summary = "批准生产领料")
    @PostMapping("/approve/{id}")
    public Result<Boolean> approveProductionMaterial(@PathVariable Long id) {
        Boolean result = productionMaterialService.approveProductionMaterial(id);
        return Result.success(result);
    }

    @Operation(summary = "领料出库")
    @PostMapping("/issue/{id}")
    public Result<Boolean> issueMaterial(@PathVariable Long id) {
        Boolean result = productionMaterialService.issueMaterial(id);
        return Result.success(result);
    }
}

package com.qoobot.qooerp.production.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.qoobot.qooerp.common.result.Result;
import com.qoobot.qooerp.production.dto.ProductionQualityDTO;
import com.qoobot.qooerp.production.service.ProductionQualityService;
import com.qoobot.qooerp.production.vo.ProductionQualityVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "生产质检管理", description = "生产质检相关接口")
@RestController
@RequestMapping("/production/quality")
@RequiredArgsConstructor
public class ProductionQualityController {

    private final ProductionQualityService productionQualityService;

    @Operation(summary = "创建生产质检")
    @PostMapping("/create")
    public Result<Long> createProductionQuality(@Valid @RequestBody ProductionQualityDTO dto) {
        Long id = productionQualityService.createProductionQuality(dto);
        return Result.success(id);
    }

    @Operation(summary = "更新生产质检")
    @PutMapping("/update/{id}")
    public Result<Boolean> updateProductionQuality(@PathVariable Long id, @Valid @RequestBody ProductionQualityDTO dto) {
        Boolean result = productionQualityService.updateProductionQuality(id, dto);
        return Result.success(result);
    }

    @Operation(summary = "删除生产质检")
    @DeleteMapping("/delete/{id}")
    public Result<Boolean> deleteProductionQuality(@PathVariable Long id) {
        Boolean result = productionQualityService.deleteProductionQuality(id);
        return Result.success(result);
    }

    @Operation(summary = "批量删除生产质检")
    @DeleteMapping("/batch-delete")
    public Result<Boolean> batchDeleteProductionQuality(@RequestBody List<Long> ids) {
        Boolean result = productionQualityService.batchDeleteProductionQuality(ids);
        return Result.success(result);
    }

    @Operation(summary = "根据ID查询生产质检")
    @GetMapping("/get/{id}")
    public Result<ProductionQualityVO> getProductionQualityById(@PathVariable Long id) {
        ProductionQualityVO vo = productionQualityService.getProductionQualityById(id);
        return Result.success(vo);
    }

    @Operation(summary = "分页查询生产质检")
    @GetMapping("/page")
    public Result<IPage<ProductionQualityVO>> queryProductionQualityPage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String qualityNo,
            @RequestParam(required = false) String status) {
        IPage<ProductionQualityVO> page = productionQualityService.queryProductionQualityPage(current, size, qualityNo, status);
        return Result.success(page);
    }

    @Operation(summary = "开始质检")
    @PostMapping("/start/{id}")
    public Result<Boolean> startInspection(@PathVariable Long id) {
        Boolean result = productionQualityService.startInspection(id);
        return Result.success(result);
    }

    @Operation(summary = "质检合格")
    @PostMapping("/pass/{id}")
    public Result<Boolean> passQualityCheck(@PathVariable Long id) {
        Boolean result = productionQualityService.passQualityCheck(id);
        return Result.success(result);
    }

    @Operation(summary = "质检不合格")
    @PostMapping("/fail/{id}")
    public Result<Boolean> failQualityCheck(@PathVariable Long id) {
        Boolean result = productionQualityService.failQualityCheck(id);
        return Result.success(result);
    }
}

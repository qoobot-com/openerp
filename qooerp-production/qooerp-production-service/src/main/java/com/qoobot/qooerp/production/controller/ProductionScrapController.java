package com.qoobot.qooerp.production.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.qoobot.qooerp.common.result.Result;
import com.qoobot.qooerp.production.entity.ProductionScrap;
import com.qoobot.qooerp.production.service.ProductionScrapService;
import com.qoobot.qooerp.production.vo.ProductionScrapVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "生产报废管理", description = "生产报废相关接口")
@RestController
@RequestMapping("/production/scrap")
@RequiredArgsConstructor
public class ProductionScrapController {

    private final ProductionScrapService productionScrapService;

    @Operation(summary = "创建生产报废")
    @PostMapping("/create")
    public Result<Long> createProductionScrap(@RequestBody ProductionScrap scrap) {
        Long id = productionScrapService.createProductionScrap(scrap);
        return Result.success(id);
    }

    @Operation(summary = "更新生产报废")
    @PutMapping("/update/{id}")
    public Result<Boolean> updateProductionScrap(@PathVariable Long id, @RequestBody ProductionScrap scrap) {
        Boolean result = productionScrapService.updateProductionScrap(id, scrap);
        return Result.success(result);
    }

    @Operation(summary = "删除生产报废")
    @DeleteMapping("/delete/{id}")
    public Result<Boolean> deleteProductionScrap(@PathVariable Long id) {
        Boolean result = productionScrapService.deleteProductionScrap(id);
        return Result.success(result);
    }

    @Operation(summary = "根据ID查询生产报废")
    @GetMapping("/get/{id}")
    public Result<ProductionScrap> getProductionScrapById(@PathVariable Long id) {
        ProductionScrap scrap = productionScrapService.getProductionScrapById(id);
        return Result.success(scrap);
    }

    @Operation(summary = "分页查询生产报废")
    @GetMapping("/page")
    public Result<IPage<ProductionScrapVO>> queryProductionScrapPage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String scrapNo,
            @RequestParam(required = false) String status) {
        IPage<ProductionScrapVO> page = productionScrapService.queryProductionScrapPage(current, size, scrapNo, status);
        return Result.success(page);
    }

    @Operation(summary = "批准报废")
    @PostMapping("/approve/{id}")
    public Result<Boolean> approveScrap(@PathVariable Long id) {
        Boolean result = productionScrapService.approveScrap(id);
        return Result.success(result);
    }
}

package com.qoobot.qooerp.production.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.qoobot.qooerp.common.result.Result;
import com.qoobot.qooerp.production.entity.ProductionRework;
import com.qoobot.qooerp.production.service.ProductionReworkService;
import com.qoobot.qooerp.production.vo.ProductionReworkVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "生产返工管理", description = "生产返工相关接口")
@RestController
@RequestMapping("/production/rework")
@RequiredArgsConstructor
public class ProductionReworkController {

    private final ProductionReworkService productionReworkService;

    @Operation(summary = "创建生产返工")
    @PostMapping("/create")
    public Result<Long> createProductionRework(@RequestBody ProductionRework rework) {
        Long id = productionReworkService.createProductionRework(rework);
        return Result.success(id);
    }

    @Operation(summary = "更新生产返工")
    @PutMapping("/update/{id}")
    public Result<Boolean> updateProductionRework(@PathVariable Long id, @RequestBody ProductionRework rework) {
        Boolean result = productionReworkService.updateProductionRework(id, rework);
        return Result.success(result);
    }

    @Operation(summary = "删除生产返工")
    @DeleteMapping("/delete/{id}")
    public Result<Boolean> deleteProductionRework(@PathVariable Long id) {
        Boolean result = productionReworkService.deleteProductionRework(id);
        return Result.success(result);
    }

    @Operation(summary = "根据ID查询生产返工")
    @GetMapping("/get/{id}")
    public Result<ProductionRework> getProductionReworkById(@PathVariable Long id) {
        ProductionRework rework = productionReworkService.getProductionReworkById(id);
        return Result.success(rework);
    }

    @Operation(summary = "分页查询生产返工")
    @GetMapping("/page")
    public Result<IPage<ProductionReworkVO>> queryProductionReworkPage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String reworkNo,
            @RequestParam(required = false) String status) {
        IPage<ProductionReworkVO> page = productionReworkService.queryProductionReworkPage(current, size, reworkNo, status);
        return Result.success(page);
    }

    @Operation(summary = "开始返工")
    @PostMapping("/start/{id}")
    public Result<Boolean> startRework(@PathVariable Long id) {
        Boolean result = productionReworkService.startRework(id);
        return Result.success(result);
    }

    @Operation(summary = "完成返工")
    @PostMapping("/complete/{id}")
    public Result<Boolean> completeRework(@PathVariable Long id) {
        Boolean result = productionReworkService.completeRework(id);
        return Result.success(result);
    }
}

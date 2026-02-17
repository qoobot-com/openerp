package com.qoobot.qooerp.production.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.qoobot.qooerp.common.result.Result;
import com.qoobot.qooerp.production.dto.ProductionPlanDTO;
import com.qoobot.qooerp.production.service.ProductionPlanService;
import com.qoobot.qooerp.production.vo.ProductionPlanVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 生产计划Controller
 */
@Tag(name = "生产计划管理", description = "生产计划相关接口")
@RestController
@RequestMapping("/production/plan")
@RequiredArgsConstructor
public class ProductionPlanController {

    private final ProductionPlanService productionPlanService;

    @Operation(summary = "创建生产计划")
    @PostMapping("/create")
    public Result<Long> createProductionPlan(@Valid @RequestBody ProductionPlanDTO dto) {
        Long id = productionPlanService.createProductionPlan(dto);
        return Result.success(id);
    }

    @Operation(summary = "更新生产计划")
    @PutMapping("/update/{id}")
    public Result<Boolean> updateProductionPlan(
            @Parameter(description = "生产计划ID") @PathVariable Long id,
            @Valid @RequestBody ProductionPlanDTO dto) {
        Boolean result = productionPlanService.updateProductionPlan(id, dto);
        return Result.success(result);
    }

    @Operation(summary = "删除生产计划")
    @DeleteMapping("/delete/{id}")
    public Result<Boolean> deleteProductionPlan(@Parameter(description = "生产计划ID") @PathVariable Long id) {
        Boolean result = productionPlanService.deleteProductionPlan(id);
        return Result.success(result);
    }

    @Operation(summary = "批量删除生产计划")
    @DeleteMapping("/batch-delete")
    public Result<Boolean> batchDeleteProductionPlan(@RequestBody List<Long> ids) {
        Boolean result = productionPlanService.batchDeleteProductionPlan(ids);
        return Result.success(result);
    }

    @Operation(summary = "根据ID查询生产计划")
    @GetMapping("/get/{id}")
    public Result<ProductionPlanVO> getProductionPlanById(@Parameter(description = "生产计划ID") @PathVariable Long id) {
        ProductionPlanVO vo = productionPlanService.getProductionPlanById(id);
        return Result.success(vo);
    }

    @Operation(summary = "分页查询生产计划")
    @GetMapping("/page")
    public Result<IPage<ProductionPlanVO>> queryProductionPlanPage(
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "计划编号") @RequestParam(required = false) String planNo,
            @Parameter(description = "计划名称") @RequestParam(required = false) String planName,
            @Parameter(description = "状态") @RequestParam(required = false) String status) {
        IPage<ProductionPlanVO> page = productionPlanService.queryProductionPlanPage(current, size, planNo, planName, status);
        return Result.success(page);
    }

    @Operation(summary = "批准生产计划")
    @PostMapping("/approve/{id}")
    public Result<Boolean> approveProductionPlan(@Parameter(description = "生产计划ID") @PathVariable Long id) {
        Boolean result = productionPlanService.approveProductionPlan(id);
        return Result.success(result);
    }

    @Operation(summary = "取消生产计划")
    @PostMapping("/cancel/{id}")
    public Result<Boolean> cancelProductionPlan(@Parameter(description = "生产计划ID") @PathVariable Long id) {
        Boolean result = productionPlanService.cancelProductionPlan(id);
        return Result.success(result);
    }

    @Operation(summary = "执行MRP计算")
    @PostMapping("/mrp/{id}")
    public Result<Boolean> executeMRP(@Parameter(description = "生产计划ID") @PathVariable Long id) {
        Boolean result = productionPlanService.executeMRP(id);
        return Result.success(result);
    }
}

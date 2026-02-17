package com.qoobot.qooerp.production.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.qoobot.qooerp.common.result.Result;
import com.qoobot.qooerp.production.dto.ProductionReportDTO;
import com.qoobot.qooerp.production.service.ProductionReportService;
import com.qoobot.qooerp.production.vo.ProductionReportVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 生产报工Controller
 */
@Tag(name = "生产报工管理", description = "生产报工相关接口")
@RestController
@RequestMapping("/production/report")
@RequiredArgsConstructor
public class ProductionReportController {

    private final ProductionReportService productionReportService;

    @Operation(summary = "创建生产报工")
    @PostMapping("/create")
    public Result<Long> createProductionReport(@Valid @RequestBody ProductionReportDTO dto) {
        Long id = productionReportService.createProductionReport(dto);
        return Result.success(id);
    }

    @Operation(summary = "更新生产报工")
    @PutMapping("/update/{id}")
    public Result<Boolean> updateProductionReport(
            @Parameter(description = "生产报工ID") @PathVariable Long id,
            @Valid @RequestBody ProductionReportDTO dto) {
        Boolean result = productionReportService.updateProductionReport(id, dto);
        return Result.success(result);
    }

    @Operation(summary = "删除生产报工")
    @DeleteMapping("/delete/{id}")
    public Result<Boolean> deleteProductionReport(@Parameter(description = "生产报工ID") @PathVariable Long id) {
        Boolean result = productionReportService.deleteProductionReport(id);
        return Result.success(result);
    }

    @Operation(summary = "批量删除生产报工")
    @DeleteMapping("/batch-delete")
    public Result<Boolean> batchDeleteProductionReport(@RequestBody List<Long> ids) {
        Boolean result = productionReportService.batchDeleteProductionReport(ids);
        return Result.success(result);
    }

    @Operation(summary = "根据ID查询生产报工")
    @GetMapping("/get/{id}")
    public Result<ProductionReportVO> getProductionReportById(@Parameter(description = "生产报工ID") @PathVariable Long id) {
        ProductionReportVO vo = productionReportService.getProductionReportById(id);
        return Result.success(vo);
    }

    @Operation(summary = "分页查询生产报工")
    @GetMapping("/page")
    public Result<IPage<ProductionReportVO>> queryProductionReportPage(
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "报工单号") @RequestParam(required = false) String reportNo,
            @Parameter(description = "状态") @RequestParam(required = false) String status,
            @Parameter(description = "工人ID") @RequestParam(required = false) Long workerId) {
        IPage<ProductionReportVO> page = productionReportService.queryProductionReportPage(current, size, reportNo, status, workerId);
        return Result.success(page);
    }

    @Operation(summary = "批准生产报工")
    @PostMapping("/approve/{id}")
    public Result<Boolean> approveProductionReport(@Parameter(description = "生产报工ID") @PathVariable Long id) {
        Boolean result = productionReportService.approveProductionReport(id);
        return Result.success(result);
    }

    @Operation(summary = "统计报工数据")
    @GetMapping("/statistics/{orderId}")
    public Result<ProductionReportService.ReportStatistics> getReportStatistics(
            @Parameter(description = "生产订单ID") @PathVariable Long orderId) {
        ProductionReportService.ReportStatistics statistics = productionReportService.getReportStatistics(orderId);
        return Result.success(statistics);
    }
}

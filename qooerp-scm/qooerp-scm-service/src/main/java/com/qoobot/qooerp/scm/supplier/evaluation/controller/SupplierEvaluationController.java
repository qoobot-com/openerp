package com.qoobot.qooerp.scm.supplier.evaluation.controller;

import com.qoobot.qooerp.common.response.PageResult;
import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.scm.supplier.evaluation.domain.SupplierEvaluation;
import com.qoobot.qooerp.scm.supplier.evaluation.dto.EvaluationQueryDTO;
import com.qoobot.qooerp.scm.supplier.evaluation.dto.SupplierEvaluationDTO;
import com.qoobot.qooerp.scm.supplier.evaluation.service.ISupplierEvaluationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 供应商评估控制器
 *
 * @author QooERP
 * @since 2026-02-17
 */
@Tag(name = "供应商评估管理", description = "供应商评估相关接口")
@RestController
@RequestMapping("/api/scm/supplier-evaluations")
@RequiredArgsConstructor
public class SupplierEvaluationController {

    private final ISupplierEvaluationService supplierEvaluationService;

    /**
     * 创建供应商评估
     */
    @Operation(summary = "创建供应商评估")
    @PostMapping
    public Result<Long> create(@RequestBody SupplierEvaluationDTO dto) {
        Long id = supplierEvaluationService.create(dto);
        return Result.success(id);
    }

    /**
     * 更新供应商评估
     */
    @Operation(summary = "更新供应商评估")
    @PutMapping("/{id}")
    public Result<Boolean> update(@PathVariable Long id, @RequestBody SupplierEvaluationDTO dto) {
        dto.setId(id);
        boolean success = supplierEvaluationService.update(dto);
        return Result.success(success);
    }

    /**
     * 删除供应商评估
     */
    @Operation(summary = "删除供应商评估")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        boolean success = supplierEvaluationService.delete(id);
        return Result.success(success);
    }

    /**
     * 获取评估详情
     */
    @Operation(summary = "获取评估详情")
    @GetMapping("/{id}")
    public Result<SupplierEvaluationDTO> getDetail(@PathVariable Long id) {
        SupplierEvaluationDTO dto = supplierEvaluationService.getDetail(id);
        return Result.success(dto);
    }

    /**
     * 分页查询评估列表
     */
    @Operation(summary = "分页查询评估列表")
    @GetMapping
    public Result<PageResult<SupplierEvaluation>> queryPage(EvaluationQueryDTO queryDTO) {
        PageResult<SupplierEvaluation> pageResult = supplierEvaluationService.queryPage(queryDTO);
        return Result.success(pageResult);
    }

    /**
     * 根据供应商ID获取评估列表
     */
    @Operation(summary = "根据供应商ID获取评估列表")
    @GetMapping("/supplier/{supplierId}")
    public Result<List<SupplierEvaluation>> getEvaluationsBySupplierId(@PathVariable Long supplierId) {
        List<SupplierEvaluation> evaluations = supplierEvaluationService.getEvaluationsBySupplierId(supplierId);
        return Result.success(evaluations);
    }

    /**
     * 获取供应商最新评估
     */
    @Operation(summary = "获取供应商最新评估")
    @GetMapping("/supplier/{supplierId}/latest")
    public Result<SupplierEvaluationDTO> getLatestEvaluation(@PathVariable Long supplierId) {
        SupplierEvaluationDTO dto = supplierEvaluationService.getLatestEvaluation(supplierId);
        return Result.success(dto);
    }
}

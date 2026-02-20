package com.qoobot.qooerp.scm.supplier.controller;

import com.qoobot.qooerp.common.response.PageResult;
import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.scm.supplier.domain.ScmSupplier;
import com.qoobot.qooerp.scm.supplier.domain.ScmSupplierEvaluation;
import com.qoobot.qooerp.scm.supplier.dto.EvaluationDTO;
import com.qoobot.qooerp.scm.supplier.dto.SupplierDTO;
import com.qoobot.qooerp.scm.supplier.dto.SupplierQueryDTO;
import com.qoobot.qooerp.scm.supplier.service.IScmSupplierService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 供应商管理Controller
 *
 * @author QooERP
 * @since 2026-02-18
 */
@Tag(name = "供应商管理")
@RestController
@RequestMapping("/api/scm/suppliers")
@RequiredArgsConstructor
public class ScmSupplierController {

    private final IScmSupplierService supplierService;

    @Operation(summary = "创建供应商")
    @PostMapping
    public Result<Long> createSupplier(@RequestBody @Valid SupplierDTO dto) {
        Long id = supplierService.createSupplier(dto);
        return Result.success(id);
    }

    @Operation(summary = "更新供应商")
    @PutMapping("/{id}")
    public Result<Boolean> updateSupplier(@PathVariable Long id, @RequestBody @Valid SupplierDTO dto) {
        boolean result = supplierService.updateSupplier(id, dto);
        return Result.success(result);
    }

    @Operation(summary = "删除供应商")
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteSupplier(@PathVariable Long id) {
        boolean result = supplierService.deleteSupplier(id);
        return Result.success(result);
    }

    @Operation(summary = "查询供应商详情")
    @GetMapping("/{id}")
    public Result<SupplierDTO> getSupplier(@PathVariable Long id) {
        SupplierDTO dto = supplierService.getSupplier(id);
        return Result.success(dto);
    }

    @Operation(summary = "分页查询供应商列表")
    @GetMapping
    public Result<PageResult<ScmSupplier>> querySuppliers(SupplierQueryDTO queryDTO) {
        PageResult<ScmSupplier> result = supplierService.querySuppliers(queryDTO);
        return Result.success(result);
    }

    @Operation(summary = "评估供应商")
    @PostMapping("/{id}/evaluate")
    public Result<Boolean> evaluateSupplier(@PathVariable Long id, @RequestBody @Valid EvaluationDTO dto) {
        boolean result = supplierService.evaluateSupplier(id, dto);
        return Result.success(result);
    }

    @Operation(summary = "获取评估历史")
    @GetMapping("/{id}/evaluations")
    public Result<List<ScmSupplierEvaluation>> getEvaluationHistory(@PathVariable Long id) {
        List<ScmSupplierEvaluation> list = supplierService.getEvaluationHistory(id);
        return Result.success(list);
    }

    @Operation(summary = "更新供应商状态")
    @PutMapping("/{id}/status")
    public Result<Boolean> updateStatus(@PathVariable Long id, @RequestParam String status) {
        boolean result = supplierService.updateStatus(id, status);
        return Result.success(result);
    }
}

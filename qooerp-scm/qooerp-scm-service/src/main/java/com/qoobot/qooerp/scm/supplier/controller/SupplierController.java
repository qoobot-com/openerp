package com.qoobot.qooerp.scm.supplier.controller;

import com.qoobot.qooerp.common.response.PageResult;
import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.scm.supplier.domain.Supplier;
import com.qoobot.qooerp.scm.supplier.dto.*;
import com.qoobot.qooerp.scm.supplier.service.ISupplierService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 供应商Controller
 *
 * @author QooERP
 * @since 2026-02-17
 */
@Tag(name = "供应商管理", description = "供应商管理相关接口")
@RestController
@RequestMapping("/api/scm/suppliers")
@RequiredArgsConstructor
public class SupplierController {

    private final ISupplierService supplierService;

    @Operation(summary = "创建供应商")
    @PostMapping
    public Result<Long> create(@RequestBody SupplierDTO dto) {
        Long id = supplierService.create(dto);
        return Result.success(id);
    }

    @Operation(summary = "更新供应商")
    @PutMapping("/{id}")
    public Result<Boolean> update(@PathVariable Long id, @RequestBody SupplierDTO dto) {
        dto.setId(id);
        boolean success = supplierService.update(dto);
        return Result.success(success);
    }

    @Operation(summary = "删除供应商")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        boolean success = supplierService.delete(id);
        return Result.success(success);
    }

    @Operation(summary = "查询供应商详情")
    @GetMapping("/{id}")
    public Result<SupplierDTO> getDetail(@PathVariable Long id) {
        SupplierDTO dto = supplierService.getDetail(id);
        return Result.success(dto);
    }

    @Operation(summary = "分页查询供应商列表")
    @GetMapping
    public Result<PageResult<Supplier>> queryPage(SupplierQueryDTO queryDTO) {
        PageResult<Supplier> result = supplierService.queryPage(queryDTO);
        return Result.success(result);
    }

    @Operation(summary = "更新供应商状态")
    @PutMapping("/{id}/status")
    public Result<Boolean> updateStatus(@PathVariable Long id, @RequestParam String status) {
        boolean success = supplierService.updateStatus(id, status);
        return Result.success(success);
    }

    @Operation(summary = "生成供应商编码")
    @GetMapping("/code/generate")
    public Result<String> generateCode() {
        String code = supplierService.generateSupplierCode();
        return Result.success(code);
    }

    /**
     * 多条件筛选供应商（含评估信息）
     */
    @Operation(summary = "多条件筛选供应商")
    @PostMapping("/filter")
    public Result<PageResult<SupplierWithEvaluationDTO>> filterSuppliers(@RequestBody SupplierFilterDTO filterDTO) {
        PageResult<SupplierWithEvaluationDTO> result = supplierService.filterSuppliers(filterDTO);
        return Result.success(result);
    }
}

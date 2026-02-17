package com.qoobot.qooerp.scm.supplier.category.controller;

import com.qoobot.qooerp.common.response.PageResult;
import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.scm.supplier.category.domain.SupplierCategory;
import com.qoobot.qooerp.scm.supplier.category.dto.SupplierCategoryDTO;
import com.qoobot.qooerp.scm.supplier.category.dto.SupplierCategoryQueryDTO;
import com.qoobot.qooerp.scm.supplier.category.dto.SupplierCategoryTreeDTO;
import com.qoobot.qooerp.scm.supplier.category.service.ISupplierCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 供应商分类控制器
 *
 * @author QooERP
 * @since 2026-02-17
 */
@Tag(name = "供应商分类管理", description = "供应商分类相关接口")
@RestController
@RequestMapping("/api/scm/supplier-categories")
@RequiredArgsConstructor
public class SupplierCategoryController {

    private final ISupplierCategoryService supplierCategoryService;

    /**
     * 创建供应商分类
     */
    @Operation(summary = "创建供应商分类")
    @PostMapping
    public Result<Long> create(@RequestBody SupplierCategoryDTO dto) {
        Long id = supplierCategoryService.create(dto);
        return Result.success(id);
    }

    /**
     * 更新供应商分类
     */
    @Operation(summary = "更新供应商分类")
    @PutMapping("/{id}")
    public Result<Boolean> update(@PathVariable Long id, @RequestBody SupplierCategoryDTO dto) {
        dto.setId(id);
        boolean success = supplierCategoryService.update(dto);
        return Result.success(success);
    }

    /**
     * 删除供应商分类
     */
    @Operation(summary = "删除供应商分类")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        boolean success = supplierCategoryService.delete(id);
        return Result.success(success);
    }

    /**
     * 获取分类详情
     */
    @Operation(summary = "获取分类详情")
    @GetMapping("/{id}")
    public Result<SupplierCategoryDTO> getDetail(@PathVariable Long id) {
        SupplierCategoryDTO dto = supplierCategoryService.getDetail(id);
        return Result.success(dto);
    }

    /**
     * 分页查询分类列表
     */
    @Operation(summary = "分页查询分类列表")
    @GetMapping
    public Result<PageResult<SupplierCategory>> queryPage(SupplierCategoryQueryDTO queryDTO) {
        PageResult<SupplierCategory> pageResult = supplierCategoryService.queryPage(queryDTO);
        return Result.success(pageResult);
    }

    /**
     * 获取分类树
     */
    @Operation(summary = "获取分类树")
    @GetMapping("/tree")
    public Result<List<SupplierCategoryTreeDTO>> getCategoryTree() {
        List<SupplierCategoryTreeDTO> tree = supplierCategoryService.getCategoryTree();
        return Result.success(tree);
    }

    /**
     * 根据父分类ID获取子分类列表
     */
    @Operation(summary = "根据父分类ID获取子分类列表")
    @GetMapping("/children/{parentId}")
    public Result<List<SupplierCategory>> getChildren(@PathVariable Long parentId) {
        List<SupplierCategory> children = supplierCategoryService.getChildrenByParentId(parentId);
        return Result.success(children);
    }

    /**
     * 更新分类状态
     */
    @Operation(summary = "更新分类状态")
    @PutMapping("/{id}/status")
    public Result<Boolean> updateStatus(@PathVariable Long id, @RequestParam String status) {
        boolean success = supplierCategoryService.updateStatus(id, status);
        return Result.success(success);
    }

    /**
     * 校验分类编码是否存在
     */
    @Operation(summary = "校验分类编码是否存在")
    @GetMapping("/check-code")
    public Result<Boolean> checkCodeExists(@RequestParam String categoryCode,
                                            @RequestParam(required = false) Long excludeId) {
        boolean exists = supplierCategoryService.checkCodeExists(categoryCode, excludeId);
        return Result.success(exists);
    }
}

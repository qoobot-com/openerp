package com.qoobot.qooerp.scm.customer.category.controller;

import com.qoobot.qooerp.common.response.PageResult;
import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.scm.customer.category.domain.CustomerCategory;
import com.qoobot.qooerp.scm.customer.category.dto.CustomerCategoryDTO;
import com.qoobot.qooerp.scm.customer.category.dto.CustomerCategoryQueryDTO;
import com.qoobot.qooerp.scm.customer.category.dto.CustomerCategoryTreeDTO;
import com.qoobot.qooerp.scm.customer.category.service.ICustomerCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 客户分类控制器
 *
 * @author QooERP
 * @since 2026-02-17
 */
@Tag(name = "客户分类管理", description = "客户分类相关接口")
@RestController
@RequestMapping("/api/scm/customer-categories")
@RequiredArgsConstructor
public class CustomerCategoryController {

    private final ICustomerCategoryService customerCategoryService;

    /**
     * 创建客户分类
     */
    @Operation(summary = "创建客户分类")
    @PostMapping
    public Result<Long> create(@RequestBody CustomerCategoryDTO dto) {
        Long id = customerCategoryService.create(dto);
        return Result.success(id);
    }

    /**
     * 更新客户分类
     */
    @Operation(summary = "更新客户分类")
    @PutMapping("/{id}")
    public Result<Boolean> update(@PathVariable Long id, @RequestBody CustomerCategoryDTO dto) {
        dto.setId(id);
        boolean success = customerCategoryService.update(dto);
        return Result.success(success);
    }

    /**
     * 删除客户分类
     */
    @Operation(summary = "删除客户分类")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        boolean success = customerCategoryService.delete(id);
        return Result.success(success);
    }

    /**
     * 获取分类详情
     */
    @Operation(summary = "获取分类详情")
    @GetMapping("/{id}")
    public Result<CustomerCategoryDTO> getDetail(@PathVariable Long id) {
        CustomerCategoryDTO dto = customerCategoryService.getDetail(id);
        return Result.success(dto);
    }

    /**
     * 分页查询分类列表
     */
    @Operation(summary = "分页查询分类列表")
    @GetMapping
    public Result<PageResult<CustomerCategory>> queryPage(CustomerCategoryQueryDTO queryDTO) {
        PageResult<CustomerCategory> pageResult = customerCategoryService.queryPage(queryDTO);
        return Result.success(pageResult);
    }

    /**
     * 获取分类树
     */
    @Operation(summary = "获取分类树")
    @GetMapping("/tree")
    public Result<List<CustomerCategoryTreeDTO>> getCategoryTree() {
        List<CustomerCategoryTreeDTO> tree = customerCategoryService.getCategoryTree();
        return Result.success(tree);
    }

    /**
     * 根据父分类ID获取子分类列表
     */
    @Operation(summary = "根据父分类ID获取子分类列表")
    @GetMapping("/children/{parentId}")
    public Result<List<CustomerCategory>> getChildren(@PathVariable Long parentId) {
        List<CustomerCategory> children = customerCategoryService.getChildrenByParentId(parentId);
        return Result.success(children);
    }

    /**
     * 更新分类状态
     */
    @Operation(summary = "更新分类状态")
    @PutMapping("/{id}/status")
    public Result<Boolean> updateStatus(@PathVariable Long id, @RequestParam String status) {
        boolean success = customerCategoryService.updateStatus(id, status);
        return Result.success(success);
    }

    /**
     * 校验分类编码是否存在
     */
    @Operation(summary = "校验分类编码是否存在")
    @GetMapping("/check-code")
    public Result<Boolean> checkCodeExists(@RequestParam String categoryCode,
                                            @RequestParam(required = false) Long excludeId) {
        boolean exists = customerCategoryService.checkCodeExists(categoryCode, excludeId);
        return Result.success(exists);
    }
}

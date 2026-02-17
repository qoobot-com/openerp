package com.qoobot.crm.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.qoobot.qooerp.common.core.page.PageParam;
import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.crm.dto.CrmCustomerDTO;
import com.qoobot.qooerp.crm.dto.CrmFollowupDTO;
import com.qoobot.qooerp.crm.service.CrmCustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * 客户管理控制器
 */
@Tag(name = "客户管理", description = "客户管理接口")
@RestController
@RequestMapping("/api/crm/customers")
@RequiredArgsConstructor
public class CrmCustomerController {

    private final CrmCustomerService customerService;

    @Operation(summary = "创建客户")
    @PostMapping
    public Result<CrmCustomerDTO> createCustomer(@Valid @RequestBody CrmCustomerDTO dto) {
        return Result.success(customerService.createCustomer(dto));
    }

    @Operation(summary = "更新客户")
    @PutMapping("/{id}")
    public Result<CrmCustomerDTO> updateCustomer(@PathVariable Long id, @Valid @RequestBody CrmCustomerDTO dto) {
        return Result.success(customerService.updateCustomer(id, dto));
    }

    @Operation(summary = "获取客户详情")
    @GetMapping("/{id}")
    public Result<CrmCustomerDTO> getCustomer(@PathVariable Long id) {
        return Result.success(customerService.getCustomer(id));
    }

    @Operation(summary = "分页查询客户")
    @GetMapping
    public Result<IPage<CrmCustomerDTO>> listCustomers(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String customerName,
            @RequestParam(required = false) Integer customerType,
            @RequestParam(required = false) Integer level,
            @RequestParam(required = false) Integer status) {
        PageParam pageParam = new PageParam(page, size);
        return Result.success(customerService.listCustomers(pageParam, customerName, customerType, level, status));
    }

    @Operation(summary = "删除客户")
    @DeleteMapping("/{id}")
    public Result<Void> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return Result.success();
    }

    @Operation(summary = "添加跟进记录")
    @PostMapping("/{id}/followups")
    public Result<Void> addFollowup(@PathVariable Long id, @Valid @RequestBody CrmFollowupDTO dto) {
        customerService.addFollowup(id, dto);
        return Result.success();
    }
}

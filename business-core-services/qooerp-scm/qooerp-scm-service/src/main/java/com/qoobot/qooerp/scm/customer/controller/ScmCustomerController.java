package com.qoobot.qooerp.scm.customer.controller;

import com.qoobot.qooerp.common.response.PageResult;
import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.scm.customer.domain.ScmCustomer;
import com.qoobot.qooerp.scm.customer.dto.CustomerDTO;
import com.qoobot.qooerp.scm.customer.dto.CustomerQueryDTO;
import com.qoobot.qooerp.scm.customer.service.IScmCustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 客户管理Controller
 *
 * @author QooERP
 * @since 2026-02-18
 */
@Tag(name = "客户管理")
@RestController
@RequestMapping("/api/scm/customers")
@RequiredArgsConstructor
public class ScmCustomerController {

    private final IScmCustomerService customerService;

    @Operation(summary = "创建客户")
    @PostMapping
    public Result<Long> createCustomer(@RequestBody @Valid CustomerDTO dto) {
        Long id = customerService.createCustomer(dto);
        return Result.success(id);
    }

    @Operation(summary = "更新客户")
    @PutMapping("/{id}")
    public Result<Boolean> updateCustomer(@PathVariable Long id, @RequestBody @Valid CustomerDTO dto) {
        boolean result = customerService.updateCustomer(id, dto);
        return Result.success(result);
    }

    @Operation(summary = "删除客户")
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteCustomer(@PathVariable Long id) {
        boolean result = customerService.deleteCustomer(id);
        return Result.success(result);
    }

    @Operation(summary = "查询客户详情")
    @GetMapping("/{id}")
    public Result<CustomerDTO> getCustomer(@PathVariable Long id) {
        CustomerDTO dto = customerService.getCustomer(id);
        return Result.success(dto);
    }

    @Operation(summary = "分页查询客户列表")
    @GetMapping
    public Result<PageResult<ScmCustomer>> queryCustomers(CustomerQueryDTO queryDTO) {
        PageResult<ScmCustomer> result = customerService.queryCustomers(queryDTO);
        return Result.success(result);
    }

    @Operation(summary = "更新客户状态")
    @PutMapping("/{id}/status")
    public Result<Boolean> updateStatus(@PathVariable Long id, @RequestParam String status) {
        boolean result = customerService.updateStatus(id, status);
        return Result.success(result);
    }
}

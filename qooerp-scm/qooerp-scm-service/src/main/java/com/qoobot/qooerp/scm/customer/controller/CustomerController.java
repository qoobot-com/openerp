package com.qoobot.qooerp.scm.customer.controller;

import com.qoobot.qooerp.common.response.PageResult;
import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.scm.customer.domain.Customer;
import com.qoobot.qooerp.scm.customer.dto.CustomerDTO;
import com.qoobot.qooerp.scm.customer.dto.CustomerQueryDTO;
import com.qoobot.qooerp.scm.customer.service.ICustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 客户Controller
 *
 * @author QooERP
 * @since 2026-02-17
 */
@Tag(name = "客户管理", description = "客户管理相关接口")
@RestController
@RequestMapping("/api/scm/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final ICustomerService customerService;

    @Operation(summary = "创建客户")
    @PostMapping
    public Result<Long> create(@RequestBody CustomerDTO dto) {
        Long id = customerService.create(dto);
        return Result.success(id);
    }

    @Operation(summary = "更新客户")
    @PutMapping("/{id}")
    public Result<Boolean> update(@PathVariable Long id, @RequestBody CustomerDTO dto) {
        dto.setId(id);
        boolean success = customerService.update(dto);
        return Result.success(success);
    }

    @Operation(summary = "删除客户")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        boolean success = customerService.delete(id);
        return Result.success(success);
    }

    @Operation(summary = "查询客户详情")
    @GetMapping("/{id}")
    public Result<CustomerDTO> getDetail(@PathVariable Long id) {
        CustomerDTO dto = customerService.getDetail(id);
        return Result.success(dto);
    }

    @Operation(summary = "分页查询客户列表")
    @GetMapping
    public Result<PageResult<Customer>> queryPage(CustomerQueryDTO queryDTO) {
        PageResult<Customer> result = customerService.queryPage(queryDTO);
        return Result.success(result);
    }

    @Operation(summary = "更新客户状态")
    @PutMapping("/{id}/status")
    public Result<Boolean> updateStatus(@PathVariable Long id, @RequestParam String status) {
        boolean success = customerService.updateStatus(id, status);
        return Result.success(success);
    }

    @Operation(summary = "生成客户编码")
    @GetMapping("/code/generate")
    public Result<String> generateCode() {
        String code = customerService.generateCustomerCode();
        return Result.success(code);
    }
}

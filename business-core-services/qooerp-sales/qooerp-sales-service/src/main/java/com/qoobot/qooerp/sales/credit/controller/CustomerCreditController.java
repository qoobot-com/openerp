package com.qoobot.qooerp.sales.credit.controller;

import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.sales.credit.dto.CreditCheckDTO;
import com.qoobot.qooerp.sales.credit.dto.CustomerCreditDTO;
import com.qoobot.qooerp.sales.credit.service.CustomerCreditService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * 客户信用控制器
 */
@Tag(name = "客户信用管理", description = "客户信用相关接口")
@RestController
@RequestMapping("/api/sales/customer-credit")
@RequiredArgsConstructor
public class CustomerCreditController {

    private final CustomerCreditService creditService;

    @Operation(summary = "设置信用额度")
    @PostMapping("/{customerId}")
    public Result<Void> setCreditLimit(
            @Parameter(description = "客户ID") @PathVariable Long customerId,
            @RequestParam BigDecimal creditLimit) {
        return creditService.setCreditLimit(customerId, creditLimit);
    }

    @Operation(summary = "查询客户信用")
    @GetMapping("/{customerId}")
    public Result<CustomerCreditDTO> getCustomerCredit(
            @Parameter(description = "客户ID") @PathVariable Long customerId) {
        return creditService.getCustomerCredit(customerId);
    }

    @Operation(summary = "检查信用额度")
    @GetMapping("/{customerId}/check")
    public Result<CreditCheckDTO> checkCredit(
            @Parameter(description = "客户ID") @PathVariable Long customerId,
            @Parameter(description = "申请金额") @RequestParam BigDecimal amount) {
        return creditService.checkCredit(customerId, amount);
    }

    @Operation(summary = "更新信用额度")
    @PutMapping("/{customerId}")
    public Result<Void> updateCredit(
            @Parameter(description = "客户ID") @PathVariable Long customerId,
            @RequestBody CustomerCreditDTO dto) {
        dto.setCustomerId(customerId);
        return creditService.updateCredit(dto);
    }
}

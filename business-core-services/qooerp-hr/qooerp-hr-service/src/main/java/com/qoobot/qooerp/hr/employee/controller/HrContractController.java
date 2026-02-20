package com.qoobot.qooerp.hr.employee.controller;

import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.hr.employee.domain.HrContract;
import com.qoobot.qooerp.hr.employee.service.IHrContractService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 员工合同管理控制器
 *
 * @author QooERP Team
 * @since 20xx-xx-xx
 */
@Slf4j
@RestController
@RequestMapping("/contract")
@RequiredArgsConstructor
@Tag(name = "员工合同管理", description = "员工合同信息管理")
@Validated
public class HrContractController {

    private final IHrContractService contractService;

    @Operation(summary = "查询合同详情")
    @GetMapping("/{id}")
    public Result<HrContract> getContractById(@PathVariable Long id) {
        HrContract contract = contractService.getContractById(id);
        return Result.success(contract);
    }

    @Operation(summary = "查询员工合同列表")
    @GetMapping("/employee/{employeeId}")
    public Result<List<HrContract>> getContractsByEmployeeId(@PathVariable Long employeeId) {
        List<HrContract> contracts = contractService.getContractsByEmployeeId(employeeId);
        return Result.success(contracts);
    }

    @Operation(summary = "创建合同")
    @PostMapping
    public Result<Void> createContract(@Validated @RequestBody HrContract contract) {
        Long contractId = contractService.createContract(contract);
        return Result.success();
    }

    @Operation(summary = "更新合同")
    @PutMapping
    public Result<Void> updateContract(@Validated @RequestBody HrContract contract) {
        contractService.updateContract(contract);
        return Result.success();
    }

    @Operation(summary = "删除合同")
    @DeleteMapping("/{id}")
    public Result<Void> deleteContract(@PathVariable Long id) {
        contractService.deleteContract(id);
        return Result.success();
    }

    @Operation(summary = "检查合同到期")
    @PostMapping("/{id}/check-expiry")
    public Result<Boolean> checkContractExpiry(@PathVariable Long id) {
        boolean isExpired = contractService.checkContractExpiry(id);
        return Result.success(isExpired);
    }
}

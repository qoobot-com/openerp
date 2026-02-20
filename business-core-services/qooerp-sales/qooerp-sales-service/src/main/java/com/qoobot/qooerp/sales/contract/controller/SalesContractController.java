package com.qoobot.qooerp.sales.contract.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.common.result.Result;
import com.qoobot.qooerp.sales.contract.dto.ContractDTO;
import com.qoobot.qooerp.sales.contract.dto.ContractQueryDTO;
import com.qoobot.qooerp.sales.contract.service.SalesContractService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 销售合同 Controller
 */
@Tag(name = "销售合同管理", description = "销售合同相关接口")
@RestController
@RequestMapping("/api/sales/contracts")
@RequiredArgsConstructor
public class SalesContractController {

    private final SalesContractService contractService;

    @Operation(summary = "创建合同")
    @PostMapping
    public Result<ContractDTO> createContract(@RequestBody ContractDTO dto) {
        return Result.success(contractService.createContract(dto));
    }

    @Operation(summary = "更新合同")
    @PutMapping("/{id}")
    public Result<ContractDTO> updateContract(@PathVariable Long id, @RequestBody ContractDTO dto) {
        return Result.success(contractService.updateContract(id, dto));
    }

    @Operation(summary = "删除合同")
    @DeleteMapping("/{id}")
    public Result<Void> deleteContract(@PathVariable Long id) {
        contractService.deleteContract(id);
        return Result.success();
    }

    @Operation(summary = "查询合同")
    @GetMapping("/{id}")
    public Result<ContractDTO> getContractById(@PathVariable Long id) {
        return Result.success(contractService.getContractById(id));
    }

    @Operation(summary = "分页查询合同")
    @GetMapping
    public Result<Page<ContractDTO>> queryContracts(ContractQueryDTO queryDTO) {
        return Result.success(contractService.queryContracts(queryDTO));
    }

    @Operation(summary = "审核合同")
    @PostMapping("/{id}/approve")
    public Result<Void> approveContract(
            @PathVariable Long id,
            @RequestParam Long approverId,
            @RequestParam String approveRemark,
            @RequestParam boolean approved) {
        contractService.approveContract(id, approverId, approveRemark, approved);
        return Result.success();
    }

    @Operation(summary = "激活合同")
    @PostMapping("/{id}/activate")
    public Result<Void> activateContract(@PathVariable Long id) {
        contractService.activateContract(id);
        return Result.success();
    }

    @Operation(summary = "终止合同")
    @PostMapping("/{id}/terminate")
    public Result<Void> terminateContract(@PathVariable Long id, @RequestParam String reason) {
        contractService.terminateContract(id, reason);
        return Result.success();
    }
}

package com.qoobot.qooerp.crm.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.qoobot.qooerp.common.dto.PageQueryDTO;
import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.crm.dto.CrmContractDTO;
import com.qoobot.qooerp.crm.service.CrmContractService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * 合同管理控制器
 */
@Tag(name = "合同管理", description = "合同管理接口")
@RestController
@RequestMapping("/api/crm/contracts")
@RequiredArgsConstructor
public class CrmContractController {

    private final CrmContractService contractService;

    @Operation(summary = "创建合同")
    @PostMapping
    public Result<CrmContractDTO> createContract(@Valid @RequestBody CrmContractDTO dto) {
        return Result.success(contractService.createContract(dto));
    }

    @Operation(summary = "更新合同")
    @PutMapping("/{id}")
    public Result<CrmContractDTO> updateContract(@PathVariable Long id, @Valid @RequestBody CrmContractDTO dto) {
        return Result.success(contractService.updateContract(id, dto));
    }

    @Operation(summary = "获取合同详情")
    @GetMapping("/{id}")
    public Result<CrmContractDTO> getContract(@PathVariable Long id) {
        return Result.success(contractService.getContract(id));
    }

    @Operation(summary = "分页查询合同")
    @GetMapping
    public Result<IPage<CrmContractDTO>> listContracts(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) Integer status) {
        PageQueryDTO pageParam = new PageQueryDTO();
        pageParam.setCurrent(page.longValue());
        pageParam.setSize(size.longValue());
        return Result.success(contractService.listContracts(pageParam, customerId, status));
    }

    @Operation(summary = "删除合同")
    @DeleteMapping("/{id}")
    public Result<Void> deleteContract(@PathVariable Long id) {
        contractService.deleteContract(id);
        return Result.success();
    }

    @Operation(summary = "审核合同")
    @PostMapping("/{id}/approve")
    public Result<Void> approveContract(@PathVariable Long id) {
        contractService.approveContract(id);
        return Result.success();
    }

    @Operation(summary = "拒绝合同")
    @PostMapping("/{id}/reject")
    public Result<Void> rejectContract(@PathVariable Long id) {
        contractService.rejectContract(id);
        return Result.success();
    }
}

package com.qoobot.qooerp.crm.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.qoobot.qooerp.common.dto.PageQueryDTO;
import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.crm.dto.CrmOpportunityDTO;
import com.qoobot.qooerp.crm.service.CrmOpportunityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * 商机管理控制器
 */
@Tag(name = "商机管理", description = "商机管理接口")
@RestController
@RequestMapping("/api/crm/opportunities")
@RequiredArgsConstructor
public class CrmOpportunityController {

    private final CrmOpportunityService opportunityService;

    @Operation(summary = "创建商机")
    @PostMapping
    public Result<CrmOpportunityDTO> createOpportunity(@Valid @RequestBody CrmOpportunityDTO dto) {
        return Result.success(opportunityService.createOpportunity(dto));
    }

    @Operation(summary = "更新商机")
    @PutMapping("/{id}")
    public Result<CrmOpportunityDTO> updateOpportunity(@PathVariable Long id, @Valid @RequestBody CrmOpportunityDTO dto) {
        return Result.success(opportunityService.updateOpportunity(id, dto));
    }

    @Operation(summary = "获取商机详情")
    @GetMapping("/{id}")
    public Result<CrmOpportunityDTO> getOpportunity(@PathVariable Long id) {
        return Result.success(opportunityService.getOpportunity(id));
    }

    @Operation(summary = "分页查询商机")
    @GetMapping
    public Result<IPage<CrmOpportunityDTO>> listOpportunities(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) Integer stage,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Long ownerId) {
        PageQueryDTO pageParam = new PageQueryDTO();
        pageParam.setCurrent(page.longValue());
        pageParam.setSize(size.longValue());
        return Result.success(opportunityService.listOpportunities(pageParam, customerId, stage, status, ownerId));
    }

    @Operation(summary = "删除商机")
    @DeleteMapping("/{id}")
    public Result<Void> deleteOpportunity(@PathVariable Long id) {
        opportunityService.deleteOpportunity(id);
        return Result.success();
    }

    @Operation(summary = "更新商机阶段")
    @PutMapping("/{id}/stage")
    public Result<Void> updateStage(@PathVariable Long id, @RequestParam Integer stage) {
        opportunityService.updateStage(id, stage);
        return Result.success();
    }

    @Operation(summary = "商机转合同")
    @PostMapping("/{id}/convert")
    public Result<Long> convertToContract(@PathVariable Long id) {
        return Result.success(opportunityService.convertToContract(id));
    }

    @Operation(summary = "获取客户商机列表")
    @GetMapping("/customer/{customerId}")
    public Result<List<CrmOpportunityDTO>> getCustomerOpportunities(@PathVariable Long customerId) {
        return Result.success(opportunityService.getCustomerOpportunities(customerId));
    }
}

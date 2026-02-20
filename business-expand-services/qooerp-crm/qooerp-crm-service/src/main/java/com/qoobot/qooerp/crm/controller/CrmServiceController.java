package com.qoobot.qooerp.crm.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.qoobot.qooerp.common.dto.PageQueryDTO;
import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.crm.dto.CrmServiceDTO;
import com.qoobot.qooerp.crm.service.CrmCustomerServiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * 客户服务控制器
 */
@Tag(name = "客户服务", description = "客户服务接口")
@RestController
@RequestMapping("/api/crm/services")
@RequiredArgsConstructor
public class CrmServiceController {

    private final CrmCustomerServiceService customerServiceService;

    @Operation(summary = "创建服务请求")
    @PostMapping
    public Result<CrmServiceDTO> createService(@Valid @RequestBody CrmServiceDTO dto) {
        return Result.success(customerServiceService.createService(dto));
    }

    @Operation(summary = "更新服务")
    @PutMapping("/{id}")
    public Result<CrmServiceDTO> updateService(@PathVariable Long id, @Valid @RequestBody CrmServiceDTO dto) {
        return Result.success(customerServiceService.updateService(id, dto));
    }

    @Operation(summary = "获取服务详情")
    @GetMapping("/{id}")
    public Result<CrmServiceDTO> getService(@PathVariable Long id) {
        return Result.success(customerServiceService.getService(id));
    }

    @Operation(summary = "分页查询服务")
    @GetMapping
    public Result<IPage<CrmServiceDTO>> listServices(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Long assigneeId) {
        PageQueryDTO pageParam = new PageQueryDTO();
        pageParam.setCurrent(page.longValue());
        pageParam.setSize(size.longValue());
        return Result.success(customerServiceService.listServices(pageParam, customerId, status, assigneeId));
    }

    @Operation(summary = "删除服务")
    @DeleteMapping("/{id}")
    public Result<Void> deleteService(@PathVariable Long id) {
        customerServiceService.deleteService(id);
        return Result.success();
    }

    @Operation(summary = "分配服务")
    @PostMapping("/{id}/assign")
    public Result<Void> assignService(
            @PathVariable Long id,
            @RequestParam Long assigneeId,
            @RequestParam String assigneeName) {
        customerServiceService.assignService(id, assigneeId, assigneeName);
        return Result.success();
    }

    @Operation(summary = "关闭服务")
    @PostMapping("/{id}/close")
    public Result<Void> closeService(@PathVariable Long id) {
        customerServiceService.closeService(id);
        return Result.success();
    }
}

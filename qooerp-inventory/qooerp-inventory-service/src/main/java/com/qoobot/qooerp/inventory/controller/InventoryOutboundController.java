package com.qoobot.qooerp.inventory.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.inventory.dto.OutboundCreateDTO;
import com.qoobot.qooerp.inventory.dto.OutboundDTO;
import com.qoobot.qooerp.inventory.dto.OutboundQueryDTO;
import com.qoobot.qooerp.inventory.service.InventoryStockOutboundService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory/outbound")
@RequiredArgsConstructor
public class InventoryOutboundController {

    private final InventoryStockOutboundService outboundService;

    @PostMapping
    public Result<Long> createOutbound(@Valid @RequestBody OutboundCreateDTO dto) {
        Long id = outboundService.createOutbound(dto);
        return Result.success(id);
    }

    @PostMapping("/{id}/submit")
    public Result<Void> submitOutbound(@PathVariable Long id) {
        outboundService.submitOutbound(id);
        return Result.success();
    }

    @PostMapping("/{id}/audit")
    public Result<Void> auditOutbound(@PathVariable Long id, @RequestParam Boolean approved) {
        outboundService.auditOutbound(id, approved);
        return Result.success();
    }

    @PostMapping("/{id}/execute")
    public Result<Void> executeOutbound(@PathVariable Long id) {
        outboundService.executeOutbound(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<OutboundDTO> getOutboundById(@PathVariable Long id) {
        OutboundDTO dto = outboundService.getOutboundById(id);
        return Result.success(dto);
    }

    @GetMapping("/page")
    public Result<Page<OutboundDTO>> queryOutbounds(OutboundQueryDTO dto) {
        Page<OutboundDTO> page = outboundService.queryOutbounds(dto);
        return Result.success(page);
    }
}

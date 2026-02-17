package com.qoobot.qooerp.inventory.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.inventory.dto.InboundCreateDTO;
import com.qoobot.qooerp.inventory.dto.InboundDTO;
import com.qoobot.qooerp.inventory.dto.InboundQueryDTO;
import com.qoobot.qooerp.inventory.service.InventoryStockInboundService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory/inbound")
@RequiredArgsConstructor
public class InventoryInboundController {

    private final InventoryStockInboundService inboundService;

    @PostMapping
    public Result<Long> createInbound(@Valid @RequestBody InboundCreateDTO dto) {
        Long id = inboundService.createInbound(dto);
        return Result.success(id);
    }

    @PostMapping("/{id}/submit")
    public Result<Void> submitInbound(@PathVariable Long id) {
        inboundService.submitInbound(id);
        return Result.success();
    }

    @PostMapping("/{id}/audit")
    public Result<Void> auditInbound(@PathVariable Long id, @RequestParam Boolean approved) {
        inboundService.auditInbound(id, approved);
        return Result.success();
    }

    @PostMapping("/{id}/execute")
    public Result<Void> executeInbound(@PathVariable Long id) {
        inboundService.executeInbound(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<InboundDTO> getInboundById(@PathVariable Long id) {
        InboundDTO dto = inboundService.getInboundById(id);
        return Result.success(dto);
    }

    @GetMapping("/page")
    public Result<Page<InboundDTO>> queryInbounds(InboundQueryDTO dto) {
        Page<InboundDTO> page = inboundService.queryInbounds(dto);
        return Result.success(page);
    }
}

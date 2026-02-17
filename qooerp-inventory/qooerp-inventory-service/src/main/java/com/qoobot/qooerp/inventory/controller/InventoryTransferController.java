package com.qoobot.qooerp.inventory.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.inventory.dto.TransferCreateDTO;
import com.qoobot.qooerp.inventory.dto.TransferDTO;
import com.qoobot.qooerp.inventory.dto.TransferQueryDTO;
import com.qoobot.qooerp.inventory.service.InventoryTransferService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory/transfer")
@RequiredArgsConstructor
public class InventoryTransferController {

    private final InventoryTransferService transferService;

    @PostMapping
    public Result<Long> createTransfer(@Valid @RequestBody TransferCreateDTO dto) {
        Long id = transferService.createTransfer(dto);
        return Result.success(id);
    }

    @PostMapping("/{id}/submit")
    public Result<Void> submitTransfer(@PathVariable Long id) {
        transferService.submitTransfer(id);
        return Result.success();
    }

    @PostMapping("/{id}/audit")
    public Result<Void> auditTransfer(@PathVariable Long id, @RequestParam Boolean approved) {
        transferService.auditTransfer(id, approved);
        return Result.success();
    }

    @PostMapping("/{id}/execute")
    public Result<Void> executeTransfer(@PathVariable Long id) {
        transferService.executeTransfer(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<TransferDTO> getTransferById(@PathVariable Long id) {
        TransferDTO dto = transferService.getTransferById(id);
        return Result.success(dto);
    }

    @GetMapping("/page")
    public Result<Page<TransferDTO>> queryTransfers(TransferQueryDTO dto) {
        Page<TransferDTO> page = transferService.queryTransfers(dto);
        return Result.success(page);
    }
}

package com.qoobot.qooerp.inventory.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.inventory.dto.CheckCreateDTO;
import com.qoobot.qooerp.inventory.dto.CheckDTO;
import com.qoobot.qooerp.inventory.dto.CheckQueryDTO;
import com.qoobot.qooerp.inventory.service.InventoryCheckService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory/check")
@RequiredArgsConstructor
public class InventoryCheckController {

    private final InventoryCheckService checkService;

    @PostMapping
    public Result<Long> createCheck(@Valid @RequestBody CheckCreateDTO dto) {
        Long id = checkService.createCheck(dto);
        return Result.success(id);
    }

    @PostMapping("/{id}/submit")
    public Result<Void> submitCheck(@PathVariable Long id) {
        checkService.submitCheck(id);
        return Result.success();
    }

    @PostMapping("/{id}/audit")
    public Result<Void> auditCheck(@PathVariable Long id, @RequestParam Boolean approved) {
        checkService.auditCheck(id, approved);
        return Result.success();
    }

    @PostMapping("/{id}/execute")
    public Result<Void> executeCheck(@PathVariable Long id) {
        checkService.executeCheck(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<CheckDTO> getCheckById(@PathVariable Long id) {
        CheckDTO dto = checkService.getCheckById(id);
        return Result.success(dto);
    }

    @GetMapping("/page")
    public Result<Page<CheckDTO>> queryChecks(CheckQueryDTO dto) {
        Page<CheckDTO> page = checkService.queryChecks(dto);
        return Result.success(page);
    }
}

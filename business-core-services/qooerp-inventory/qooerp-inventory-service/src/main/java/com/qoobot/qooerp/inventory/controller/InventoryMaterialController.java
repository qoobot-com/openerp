package com.qoobot.qooerp.inventory.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.inventory.dto.MaterialCreateDTO;
import com.qoobot.qooerp.inventory.dto.MaterialDTO;
import com.qoobot.qooerp.inventory.dto.MaterialQueryDTO;
import com.qoobot.qooerp.inventory.dto.MaterialUpdateDTO;
import com.qoobot.qooerp.inventory.service.InventoryMaterialService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory/material")
@RequiredArgsConstructor
public class InventoryMaterialController {

    private final InventoryMaterialService materialService;

    @PostMapping
    public Result<Long> createMaterial(@Valid @RequestBody MaterialCreateDTO dto) {
        Long id = materialService.createMaterial(dto);
        return Result.success(id);
    }

    @PutMapping
    public Result<Void> updateMaterial(@Valid @RequestBody MaterialUpdateDTO dto) {
        materialService.updateMaterial(dto);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteMaterial(@PathVariable Long id) {
        materialService.deleteMaterial(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<MaterialDTO> getMaterialById(@PathVariable Long id) {
        MaterialDTO dto = materialService.getMaterialById(id);
        return Result.success(dto);
    }

    @GetMapping("/page")
    public Result<Page<MaterialDTO>> queryMaterials(MaterialQueryDTO dto) {
        Page<MaterialDTO> page = materialService.queryMaterials(dto);
        return Result.success(page);
    }
}

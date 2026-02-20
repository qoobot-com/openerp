package com.qoobot.qooerp.inventory.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.inventory.dto.WarehouseCreateDTO;
import com.qoobot.qooerp.inventory.dto.WarehouseDTO;
import com.qoobot.qooerp.inventory.dto.WarehouseQueryDTO;
import com.qoobot.qooerp.inventory.dto.WarehouseUpdateDTO;
import com.qoobot.qooerp.inventory.service.InventoryWarehouseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory/warehouse")
@RequiredArgsConstructor
public class InventoryWarehouseController {

    private final InventoryWarehouseService warehouseService;

    @PostMapping
    public Result<Long> createWarehouse(@Valid @RequestBody WarehouseCreateDTO dto) {
        Long id = warehouseService.createWarehouse(dto);
        return Result.success(id);
    }

    @PutMapping
    public Result<Void> updateWarehouse(@Valid @RequestBody WarehouseUpdateDTO dto) {
        warehouseService.updateWarehouse(dto);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteWarehouse(@PathVariable Long id) {
        warehouseService.deleteWarehouse(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<WarehouseDTO> getWarehouseById(@PathVariable Long id) {
        WarehouseDTO dto = warehouseService.getWarehouseById(id);
        return Result.success(dto);
    }

    @GetMapping("/page")
    public Result<Page<WarehouseDTO>> queryWarehouses(WarehouseQueryDTO dto) {
        Page<WarehouseDTO> page = warehouseService.queryWarehouses(dto);
        return Result.success(page);
    }

    @GetMapping("/all")
    public Result<List<WarehouseDTO>> getAllWarehouses() {
        List<WarehouseDTO> list = warehouseService.getAllWarehouses();
        return Result.success(list);
    }
}

package com.qoobot.qooerp.production.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.qoobot.qooerp.common.result.Result;
import com.qoobot.qooerp.production.entity.ProductionEquipment;
import com.qoobot.qooerp.production.service.ProductionEquipmentService;
import com.qoobot.qooerp.production.vo.ProductionEquipmentVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "生产设备管理", description = "生产设备相关接口")
@RestController
@RequestMapping("/production/equipment")
@RequiredArgsConstructor
public class ProductionEquipmentController {

    private final ProductionEquipmentService productionEquipmentService;

    @Operation(summary = "创建生产设备")
    @PostMapping("/create")
    public Result<Long> createProductionEquipment(@RequestBody ProductionEquipment equipment) {
        Long id = productionEquipmentService.createProductionEquipment(equipment);
        return Result.success(id);
    }

    @Operation(summary = "更新生产设备")
    @PutMapping("/update/{id}")
    public Result<Boolean> updateProductionEquipment(@PathVariable Long id, @RequestBody ProductionEquipment equipment) {
        Boolean result = productionEquipmentService.updateProductionEquipment(id, equipment);
        return Result.success(result);
    }

    @Operation(summary = "删除生产设备")
    @DeleteMapping("/delete/{id}")
    public Result<Boolean> deleteProductionEquipment(@PathVariable Long id) {
        Boolean result = productionEquipmentService.deleteProductionEquipment(id);
        return Result.success(result);
    }

    @Operation(summary = "根据ID查询生产设备")
    @GetMapping("/get/{id}")
    public Result<ProductionEquipment> getProductionEquipmentById(@PathVariable Long id) {
        ProductionEquipment equipment = productionEquipmentService.getProductionEquipmentById(id);
        return Result.success(equipment);
    }

    @Operation(summary = "分页查询生产设备")
    @GetMapping("/page")
    public Result<IPage<ProductionEquipmentVO>> queryProductionEquipmentPage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String equipmentCode,
            @RequestParam(required = false) String equipmentName,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long workshopId) {
        IPage<ProductionEquipmentVO> page = productionEquipmentService.queryProductionEquipmentPage(current, size, equipmentCode, equipmentName, status, workshopId);
        return Result.success(page);
    }
}

package com.qoobot.qooerp.production.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.qoobot.qooerp.common.result.Result;
import com.qoobot.qooerp.production.entity.ProductionSchedule;
import com.qoobot.qooerp.production.service.ProductionScheduleService;
import com.qoobot.qooerp.production.vo.ProductionScheduleVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "生产排程管理", description = "生产排程相关接口")
@RestController
@RequestMapping("/production/schedule")
@RequiredArgsConstructor
public class ProductionScheduleController {

    private final ProductionScheduleService productionScheduleService;

    @Operation(summary = "创建生产排程")
    @PostMapping("/create")
    public Result<Long> createProductionSchedule(@RequestBody ProductionSchedule schedule) {
        Long id = productionScheduleService.createProductionSchedule(schedule);
        return Result.success(id);
    }

    @Operation(summary = "更新生产排程")
    @PutMapping("/update/{id}")
    public Result<Boolean> updateProductionSchedule(@PathVariable Long id, @RequestBody ProductionSchedule schedule) {
        Boolean result = productionScheduleService.updateProductionSchedule(id, schedule);
        return Result.success(result);
    }

    @Operation(summary = "删除生产排程")
    @DeleteMapping("/delete/{id}")
    public Result<Boolean> deleteProductionSchedule(@PathVariable Long id) {
        Boolean result = productionScheduleService.deleteProductionSchedule(id);
        return Result.success(result);
    }

    @Operation(summary = "根据ID查询生产排程")
    @GetMapping("/get/{id}")
    public Result<ProductionSchedule> getProductionScheduleById(@PathVariable Long id) {
        ProductionSchedule schedule = productionScheduleService.getProductionScheduleById(id);
        return Result.success(schedule);
    }

    @Operation(summary = "分页查询生产排程")
    @GetMapping("/page")
    public Result<IPage<ProductionScheduleVO>> queryProductionSchedulePage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String scheduleCode,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long equipmentId) {
        IPage<ProductionScheduleVO> page = productionScheduleService.queryProductionSchedulePage(current, size, scheduleCode, status, equipmentId);
        return Result.success(page);
    }

    @Operation(summary = "开始排程")
    @PostMapping("/start/{id}")
    public Result<Boolean> startSchedule(@PathVariable Long id) {
        Boolean result = productionScheduleService.startSchedule(id);
        return Result.success(result);
    }

    @Operation(summary = "完成排程")
    @PostMapping("/complete/{id}")
    public Result<Boolean> completeSchedule(@PathVariable Long id) {
        Boolean result = productionScheduleService.completeSchedule(id);
        return Result.success(result);
    }
}

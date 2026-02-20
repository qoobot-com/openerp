package com.qoobot.qooerp.hr.schedule.controller;

import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.hr.schedule.domain.HrShift;
import com.qoobot.qooerp.hr.schedule.service.IHrShiftService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 班次管理控制器
 *
 * @author QooERP Team
 * @since 20xx-xx-xx
 */
@Slf4j
@RestController
@RequestMapping("/shift")
@RequiredArgsConstructor
@Tag(name = "班次管理", description = "班次管理")
@Validated
public class HrShiftController {

    private final IHrShiftService shiftService;

    @Operation(summary = "创建班次")
    @PostMapping
    public Result<Long> createShift(@Valid @RequestBody HrShift shift) {
        Long shiftId = shiftService.createShift(shift);
        return Result.success(shiftId);
    }

    @Operation(summary = "更新班次")
    @PutMapping
    public Result<Boolean> updateShift(@Valid @RequestBody HrShift shift) {
        Boolean result = shiftService.updateShift(shift);
        return Result.success(result);
    }

    @Operation(summary = "删除班次")
    @DeleteMapping("/{shiftId}")
    public Result<Boolean> deleteShift(@PathVariable Long shiftId) {
        Boolean result = shiftService.deleteShift(shiftId);
        return Result.success(result);
    }

    @Operation(summary = "获取班次详情")
    @GetMapping("/{shiftId}")
    public Result<HrShift> getShift(@PathVariable Long shiftId) {
        HrShift shift = shiftService.getById(shiftId);
        return Result.success(shift);
    }

    @Operation(summary = "获取启用的班次列表")
    @GetMapping("/active")
    public Result<List<HrShift>> getActiveShifts() {
        List<HrShift> list = shiftService.getActiveShifts();
        return Result.success(list);
    }
}

package com.qoobot.qooerp.hr.performance.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.common.result.Result;
import com.qoobot.qooerp.hr.performance.domain.HrKpiIndicator;
import com.qoobot.qooerp.hr.performance.service.IHrKpiIndicatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * KPI指标控制器
 */
@RestController
@RequestMapping("/api/hr/kpi-indicator")
@RequiredArgsConstructor
public class HrKpiIndicatorController {

    private final IHrKpiIndicatorService kpiIndicatorService;

    @PostMapping("/create")
    public Result<HrKpiIndicator> create(@RequestBody HrKpiIndicator indicator) {
        return kpiIndicatorService.createIndicator(indicator);
    }

    @PutMapping("/update")
    public Result<Boolean> update(@RequestBody HrKpiIndicator indicator) {
        return kpiIndicatorService.updateIndicator(indicator);
    }

    @DeleteMapping("/delete/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return kpiIndicatorService.deleteIndicator(id);
    }

    @GetMapping("/get/{id}")
    public Result<HrKpiIndicator> get(@PathVariable Long id) {
        return kpiIndicatorService.getIndicator(id);
    }

    @GetMapping("/list")
    public Result<IPage<HrKpiIndicator>> list(
            @RequestParam(required = false) Integer indicatorType,
            @RequestParam(required = false) Boolean isEnabled,
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {
        return kpiIndicatorService.listIndicators(new Page<>(current, size), indicatorType, isEnabled);
    }

    @GetMapping("/enabled")
    public Result<List<HrKpiIndicator>> listEnabled() {
        return kpiIndicatorService.listEnabledIndicators();
    }

    @GetMapping("/by-position/{positionId}")
    public Result<List<HrKpiIndicator>> listByPosition(@PathVariable Long positionId) {
        return kpiIndicatorService.listIndicatorsByPosition(positionId);
    }

    @GetMapping("/by-type/{indicatorType}")
    public Result<List<HrKpiIndicator>> listByType(@PathVariable Integer indicatorType) {
        return kpiIndicatorService.listIndicatorsByType(indicatorType);
    }
}

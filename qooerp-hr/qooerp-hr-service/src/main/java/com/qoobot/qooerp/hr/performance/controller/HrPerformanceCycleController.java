package com.qoobot.qooerp.hr.performance.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.common.result.Result;
import com.qoobot.qooerp.hr.performance.domain.HrPerformanceCycle;
import com.qoobot.qooerp.hr.performance.service.IHrPerformanceCycleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 绩效周期控制器
 */
@RestController
@RequestMapping("/api/hr/performance-cycle")
@RequiredArgsConstructor
public class HrPerformanceCycleController {

    private final IHrPerformanceCycleService performanceCycleService;

    @PostMapping("/create")
    public Result<HrPerformanceCycle> create(@RequestBody HrPerformanceCycle cycle) {
        return performanceCycleService.createCycle(cycle);
    }

    @PutMapping("/update")
    public Result<Boolean> update(@RequestBody HrPerformanceCycle cycle) {
        return performanceCycleService.updateCycle(cycle);
    }

    @DeleteMapping("/delete/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return performanceCycleService.deleteCycle(id);
    }

    @PostMapping("/enable/{id}")
    public Result<Boolean> enable(@PathVariable Long id) {
        return performanceCycleService.enableCycle(id);
    }

    @GetMapping("/get/{id}")
    public Result<HrPerformanceCycle> get(@PathVariable Long id) {
        return performanceCycleService.getCycle(id);
    }

    @GetMapping("/list")
    public Result<IPage<HrPerformanceCycle>> list(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer cycleType,
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {
        return performanceCycleService.listCycles(new Page<>(current, size), year, cycleType);
    }

    @GetMapping("/enabled")
    public Result<List<HrPerformanceCycle>> listEnabled() {
        return performanceCycleService.listEnabledCycles();
    }

    @GetMapping("/current")
    public Result<HrPerformanceCycle> getCurrent() {
        return performanceCycleService.getCurrentCycle();
    }

    @PostMapping("/start/{id}")
    public Result<Boolean> start(@PathVariable Long id) {
        return performanceCycleService.startCycle(id);
    }

    @PostMapping("/end/{id}")
    public Result<Boolean> end(@PathVariable Long id) {
        return performanceCycleService.endCycle(id);
    }
}

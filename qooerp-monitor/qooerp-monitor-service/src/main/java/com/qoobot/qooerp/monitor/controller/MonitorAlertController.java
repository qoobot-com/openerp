package com.qoobot.qooerp.monitor.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.monitor.dto.*;
import com.qoobot.qooerp.monitor.service.MonitorAlertService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/monitor/alert")
@RequiredArgsConstructor
public class MonitorAlertController {

    private final MonitorAlertService alertService;

    @PostMapping("/list")
    public Result<Page<MonitorAlertDTO>> listAlerts(@RequestBody AlertQueryDTO dto) {
        Page<MonitorAlertDTO> page = alertService.listAlerts(dto);
        return Result.success(page);
    }

    @GetMapping("/{id}")
    public Result<MonitorAlertDTO> getAlert(@PathVariable Long id) {
        MonitorAlertDTO dto = alertService.getAlert(id);
        return Result.success(dto);
    }

    @PostMapping("/handle/{id}")
    public Result<Void> handleAlert(@PathVariable Long id, @Valid @RequestBody AlertHandleDTO dto) {
        alertService.handleAlert(id, dto);
        return Result.success();
    }

    @GetMapping("/statistics")
    public Result<AlertStatisticsDTO> getStatistics(AlertStatisticsQueryDTO dto) {
        AlertStatisticsDTO statistics = alertService.getStatistics(dto);
        return Result.success(statistics);
    }
}

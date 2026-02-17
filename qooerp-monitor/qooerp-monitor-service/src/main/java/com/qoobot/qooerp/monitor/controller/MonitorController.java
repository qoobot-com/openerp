package com.qoobot.qooerp.monitor.controller;

import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.monitor.dto.*;
import com.qoobot.qooerp.monitor.service.MonitorServiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/monitor/service")
@RequiredArgsConstructor
public class MonitorController {

    private final MonitorServiceService serviceService;

    @PostMapping("/register")
    public Result<Long> registerService(@Valid @RequestBody MonitorServiceCreateDTO dto) {
        Long id = serviceService.registerService(dto);
        return Result.success(id);
    }

    @PutMapping("/{id}")
    public Result<Void> updateService(@PathVariable Long id, @Valid @RequestBody MonitorServiceUpdateDTO dto) {
        serviceService.updateService(id, dto);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteService(@PathVariable Long id) {
        serviceService.deleteService(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<MonitorServiceDTO> getService(@PathVariable Long id) {
        MonitorServiceDTO dto = serviceService.getService(id);
        return Result.success(dto);
    }

    @GetMapping("/list")
    public Result<List<MonitorServiceDTO>> listServices() {
        List<MonitorServiceDTO> list = serviceService.listServices();
        return Result.success(list);
    }

    @PostMapping("/check")
    public Result<Void> healthCheck(@Valid @RequestBody ServiceCheckDTO dto) {
        serviceService.healthCheck(dto);
        return Result.success();
    }
}

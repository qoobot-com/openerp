package com.qoobot.qooerp.monitor.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.monitor.dto.LogQueryDTO;
import com.qoobot.qooerp.monitor.dto.MonitorLogDTO;
import com.qoobot.qooerp.monitor.service.MonitorLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/monitor/log")
@RequiredArgsConstructor
public class MonitorLogController {

    private final MonitorLogService logService;

    @PostMapping("/list")
    public Result<Page<MonitorLogDTO>> listLogs(@RequestBody LogQueryDTO dto) {
        Page<MonitorLogDTO> page = logService.listLogs(dto);
        return Result.success(page);
    }

    @GetMapping("/types")
    public Result<List<String>> listLogTypes() {
        List<String> types = logService.listLogTypes();
        return Result.success(types);
    }
}

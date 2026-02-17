package com.qoobot.qooerp.monitor.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.monitor.dto.MonitorTraceDTO;
import com.qoobot.qooerp.monitor.dto.TraceQueryDTO;
import com.qoobot.qooerp.monitor.service.MonitorTraceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/monitor/trace")
@RequiredArgsConstructor
public class MonitorTraceController {

    private final MonitorTraceService traceService;

    @GetMapping("/{traceId}")
    public Result<List<MonitorTraceDTO>> getTrace(@PathVariable String traceId) {
        List<MonitorTraceDTO> traces = traceService.getTrace(traceId);
        return Result.success(traces);
    }

    @PostMapping("/list")
    public Result<Page<MonitorTraceDTO>> listTraces(@RequestBody TraceQueryDTO dto) {
        Page<MonitorTraceDTO> page = traceService.listTraces(dto);
        return Result.success(page);
    }

    @GetMapping("/spans/{traceId}")
    public Result<List<MonitorTraceDTO>> getSpans(@PathVariable String traceId) {
        List<MonitorTraceDTO> spans = traceService.getSpans(traceId);
        return Result.success(spans);
    }
}

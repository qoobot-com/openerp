package com.qoobot.qooerp.scheduler.log.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.common.result.Result;
import com.qoobot.qooerp.scheduler.job.dto.ScheduleLogDTO;
import com.qoobot.qooerp.scheduler.log.service.ScheduleLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 任务日志 Controller
 */
@Tag(name = "任务日志管理", description = "任务日志相关接口")
@RestController
@RequestMapping("/logs")
@RequiredArgsConstructor
public class ScheduleLogController {

    private final ScheduleLogService logService;

    @Operation(summary = "查询执行日志")
    @GetMapping
    public Result<Page<ScheduleLogDTO>> query(
            @RequestParam Long jobId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(logService.queryLogs(jobId, startTime, endTime, current, size));
    }

    @Operation(summary = "获取最近日志")
    @GetMapping("/recent/{jobId}")
    public Result<List<ScheduleLogDTO>> getRecent(
            @PathVariable Long jobId,
            @RequestParam(defaultValue = "10") Integer limit) {
        return Result.success(logService.getRecentLogs(jobId, limit));
    }
}

package com.qoobot.qooerp.scheduler.config.controller;

import com.qoobot.qooerp.common.result.Result;
import com.qoobot.qooerp.scheduler.config.service.ScheduleConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 任务配置 Controller
 */
@Tag(name = "任务配置管理", description = "任务配置相关接口")
@RestController
@RequestMapping("/configs")
@RequiredArgsConstructor
public class ScheduleConfigController {

    private final ScheduleConfigService configService;

    @Operation(summary = "设置任务配置")
    @PostMapping
    public Result<Void> setConfig(
            @RequestParam Long jobId,
            @RequestParam String configKey,
            @RequestParam String configValue,
            @RequestParam(required = false) String configDesc) {
        configService.setConfig(jobId, configKey, configValue, configDesc);
        return Result.success();
    }

    @Operation(summary = "获取任务配置")
    @GetMapping("/{jobId}")
    public Result<Map<String, String>> getConfig(@PathVariable Long jobId) {
        return Result.success(configService.getConfig(jobId));
    }

    @Operation(summary = "删除任务配置")
    @DeleteMapping("/{jobId}/{configKey}")
    public Result<Void> deleteConfig(@PathVariable Long jobId, @PathVariable String configKey) {
        configService.deleteConfig(jobId, configKey);
        return Result.success();
    }
}

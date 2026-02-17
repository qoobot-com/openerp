package com.qoobot.qooerp.notify.controller;

import com.qoobot.qooerp.notify.common.Result;
import com.qoobot.qooerp.notify.dto.ChannelConfigDTO;
import com.qoobot.qooerp.notify.service.ConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 配置管理控制器
 */
@Tag(name = "配置管理", description = "配置管理相关接口")
@RestController
@RequestMapping("/config")
@RequiredArgsConstructor
public class ConfigController {

    private final ConfigService configService;

    @Operation(summary = "获取配置")
    @GetMapping("/getConfig")
    public Result<Object> getConfig(@RequestParam String key) {
        Object value = configService.getConfig(key);
        return Result.success(value);
    }

    @Operation(summary = "更新配置")
    @PostMapping("/updateConfig")
    public Result<Void> updateConfig(@RequestParam String key, @RequestParam String value) {
        configService.updateConfig(key, value);
        return Result.success();
    }

    @Operation(summary = "获取所有配置")
    @GetMapping("/getAll")
    public Result<Map<String, Object>> getAllConfigs() {
        Map<String, Object> configs = configService.getAllConfigs();
        return Result.success(configs);
    }

    @Operation(summary = "更新渠道配置")
    @PostMapping("/updateChannelConfig")
    public Result<Void> updateChannelConfig(@Valid @RequestBody ChannelConfigDTO dto) {
        configService.updateChannelConfig(dto);
        return Result.success();
    }
}

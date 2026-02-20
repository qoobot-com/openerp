package com.qoobot.qooerp.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.system.dto.SystemConfigDTO;
import com.qoobot.qooerp.system.dto.SystemConfigQueryDTO;
import com.qoobot.qooerp.system.service.SystemConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 系统参数控制器
 */
@RestController
@RequestMapping("/api/system/config")
@RequiredArgsConstructor
public class SystemConfigController {

    private final SystemConfigService configService;

    /**
     * 创建参数
     */
    @PostMapping
    public Result<Long> createConfig(@RequestBody SystemConfigDTO configDTO) {
        Long id = configService.createConfig(configDTO);
        return Result.success(id);
    }

    /**
     * 获取参数
     */
    @GetMapping("/{id}")
    public Result<SystemConfigDTO> getConfig(@PathVariable Long id) {
        SystemConfigDTO configDTO = configService.getConfig(id);
        return Result.success(configDTO);
    }

    /**
     * 更新参数
     */
    @PutMapping("/{id}")
    public Result<Void> updateConfig(@PathVariable Long id, @RequestBody SystemConfigDTO configDTO) {
        configService.updateConfig(id, configDTO);
        return Result.success();
    }

    /**
     * 删除参数
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteConfig(@PathVariable Long id) {
        configService.deleteConfig(id);
        return Result.success();
    }

    /**
     * 分页查询参数
     */
    @GetMapping("/list")
    public Result<IPage<SystemConfigDTO>> pageConfig(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            SystemConfigQueryDTO queryDTO) {
        IPage<SystemConfigDTO> page = configService.pageConfig(current, size, queryDTO);
        return Result.success(page);
    }

    /**
     * 根据键获取参数值
     */
    @GetMapping("/value/{key}")
    public Result<String> getConfigValue(@PathVariable String key) {
        String value = configService.getConfigValue(key);
        return Result.success(value);
    }

    /**
     * 根据键获取参数
     */
    @GetMapping("/key/{key}")
    public Result<SystemConfigDTO> getConfigByKey(@PathVariable String key) {
        SystemConfigDTO configDTO = configService.getConfigByKey(key);
        return Result.success(configDTO);
    }

    /**
     * 批量更新参数
     */
    @PutMapping("/batch")
    public Result<Void> batchUpdateConfig(@RequestBody Map<String, String> configMap) {
        configService.batchUpdateConfig(configMap);
        return Result.success();
    }
}

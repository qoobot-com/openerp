package com.qoobot.qooerp.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.system.dto.SystemConfigDTO;
import com.qoobot.qooerp.system.service.SystemConfigService;
import com.qoobot.qooerp.system.vo.SystemConfigVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 系统参数管理Controller
 *
 * @author QooERP Team
 * @version 1.0.0
 */
@Tag(name = "系统参数管理", description = "系统参数管理接口")
@RestController
@RequestMapping("/api/system/config")
@RequiredArgsConstructor
public class SystemConfigController {

    private final SystemConfigService configService;

    @Operation(summary = "创建参数")
    @PostMapping
    public Result<Long> create(@Valid @RequestBody SystemConfigDTO dto) {
        Long id = configService.createConfig(dto);
        return Result.success(id);
    }

    @Operation(summary = "获取参数详情")
    @GetMapping("/{id}")
    public Result<SystemConfigVO> getById(@Parameter(description = "参数ID") @PathVariable Long id) {
        SystemConfigVO vo = configService.getConfig(id);
        return Result.success(vo);
    }

    @Operation(summary = "根据键获取参数值")
    @GetMapping("/key/{key}")
    public Result<String> getValueByKey(@Parameter(description = "参数键") @PathVariable String key) {
        String value = configService.getConfigValue(key);
        return Result.success(value);
    }

    @Operation(summary = "根据键获取参数详情")
    @GetMapping("/key/detail/{key}")
    public Result<SystemConfigVO> getByKey(@Parameter(description = "参数键") @PathVariable String key) {
        SystemConfigVO vo = configService.getConfigByKey(key);
        return Result.success(vo);
    }

    @Operation(summary = "更新参数")
    @PutMapping("/{id}")
    public Result<Void> update(@Parameter(description = "参数ID") @PathVariable Long id,
                                @Valid @RequestBody SystemConfigDTO dto) {
        configService.updateConfig(id, dto);
        return Result.success();
    }

    @Operation(summary = "删除参数")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@Parameter(description = "参数ID") @PathVariable Long id) {
        configService.deleteConfig(id);
        return Result.success();
    }

    @Operation(summary = "分页查询参数")
    @GetMapping("/list")
    public Result<Page<SystemConfigVO>> page(
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Long size,
            @Parameter(description = "参数键") @RequestParam(required = false) String configKey,
            @Parameter(description = "参数名称") @RequestParam(required = false) String configName) {
        Page<SystemConfigVO> page = configService.pageConfig(current, size, configKey, configName);
        return Result.success(page);
    }

    @Operation(summary = "批量更新参数")
    @PostMapping("/batch")
    public Result<Void> batchUpdate(@RequestBody List<SystemConfigDTO> dtoList) {
        configService.batchUpdateConfig(dtoList);
        return Result.success();
    }

    @Operation(summary = "刷新参数缓存")
    @PostMapping("/refresh")
    public Result<Void> refreshCache() {
        configService.refreshCache();
        return Result.success();
    }
}

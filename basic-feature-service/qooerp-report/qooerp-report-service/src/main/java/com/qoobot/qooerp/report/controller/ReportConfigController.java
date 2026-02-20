package com.qoobot.qooerp.report.controller;

import com.qoobot.qooerp.common.response.PageResult;
import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.report.dto.ReportConfigCreateDTO;
import com.qoobot.qooerp.report.dto.ReportConfigDTO;
import com.qoobot.qooerp.report.dto.ReportConfigQueryDTO;
import com.qoobot.qooerp.report.dto.ReportConfigUpdateDTO;
import com.qoobot.qooerp.report.service.ReportConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 报表配置管理控制器
 *
 * @author Auto
 * @since 2026-02-18
 */
@Slf4j
@RestController
@RequestMapping("/configs")
public class ReportConfigController {

    @Autowired
    private ReportConfigService configService;

    /**
     * 创建配置
     */
    @PostMapping
    public Result<ReportConfigDTO> createConfig(@Validated @RequestBody ReportConfigCreateDTO dto) {
        log.info("创建配置: {}", dto);
        return Result.success(configService.createConfig(dto));
    }

    /**
     * 更新配置
     */
    @PutMapping("/{id}")
    public Result<Boolean> updateConfig(@PathVariable Long id, @Validated @RequestBody ReportConfigUpdateDTO dto) {
        log.info("更新配置: id={}, {}", id, dto);
        dto.setId(id);
        return Result.success(configService.updateConfig(dto));
    }

    /**
     * 删除配置
     */
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteConfig(@PathVariable Long id) {
        log.info("删除配置: id={}", id);
        return Result.success(configService.deleteConfig(id));
    }

    /**
     * 获取配置详情
     */
    @GetMapping("/{id}")
    public Result<ReportConfigDTO> getConfigById(@PathVariable Long id) {
        log.info("获取配置详情: id={}", id);
        return Result.success(configService.getConfigById(id));
    }

    /**
     * 分页查询配置
     */
    @GetMapping
    public Result<PageResult<ReportConfigDTO>> queryConfigs(ReportConfigQueryDTO queryDTO) {
        log.info("分页查询配置: {}", queryDTO);
        return Result.success(configService.queryConfigs(queryDTO));
    }
}

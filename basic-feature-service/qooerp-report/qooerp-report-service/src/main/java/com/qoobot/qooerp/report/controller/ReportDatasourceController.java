package com.qoobot.qooerp.report.controller;

import com.qoobot.qooerp.common.response.PageResult;
import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.report.dto.ReportDatasourceCreateDTO;
import com.qoobot.qooerp.report.dto.ReportDatasourceDTO;
import com.qoobot.qooerp.report.dto.ReportDatasourceQueryDTO;
import com.qoobot.qooerp.report.dto.ReportDatasourceUpdateDTO;
import com.qoobot.qooerp.report.service.ReportDatasourceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 报表数据源管理控制器
 *
 * @author Auto
 * @since 2026-02-18
 */
@Slf4j
@RestController
@RequestMapping("/datasources")
public class ReportDatasourceController {

    @Autowired
    private ReportDatasourceService datasourceService;

    /**
     * 创建数据源
     */
    @PostMapping
    public Result<ReportDatasourceDTO> createDatasource(@Validated @RequestBody ReportDatasourceCreateDTO dto) {
        log.info("创建数据源: {}", dto);
        return Result.success(datasourceService.createDatasource(dto));
    }

    /**
     * 更新数据源
     */
    @PutMapping("/{id}")
    public Result<Boolean> updateDatasource(@PathVariable Long id, @Validated @RequestBody ReportDatasourceUpdateDTO dto) {
        log.info("更新数据源: id={}, {}", id, dto);
        dto.setId(id);
        return Result.success(datasourceService.updateDatasource(dto));
    }

    /**
     * 删除数据源
     */
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteDatasource(@PathVariable Long id) {
        log.info("删除数据源: id={}", id);
        return Result.success(datasourceService.deleteDatasource(id));
    }

    /**
     * 获取数据源详情
     */
    @GetMapping("/{id}")
    public Result<ReportDatasourceDTO> getDatasourceById(@PathVariable Long id) {
        log.info("获取数据源详情: id={}", id);
        return Result.success(datasourceService.getDatasourceById(id));
    }

    /**
     * 分页查询数据源
     */
    @GetMapping
    public Result<PageResult<ReportDatasourceDTO>> queryDatasources(ReportDatasourceQueryDTO queryDTO) {
        log.info("分页查询数据源: {}", queryDTO);
        return Result.success(datasourceService.queryDatasources(queryDTO));
    }

    /**
     * 测试数据源连接
     */
    @PostMapping("/{id}/test")
    public Result<Boolean> testConnection(@PathVariable Long id) {
        log.info("测试数据源连接: id={}", id);
        return Result.success(datasourceService.testConnection(id));
    }
}

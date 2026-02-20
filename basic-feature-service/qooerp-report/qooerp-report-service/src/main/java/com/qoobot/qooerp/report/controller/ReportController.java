package com.qoobot.qooerp.report.controller;

import com.qoobot.qooerp.common.response.PageResult;
import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.report.dto.ReportCreateDTO;
import com.qoobot.qooerp.report.dto.ReportDTO;
import com.qoobot.qooerp.report.dto.ReportQueryDTO;
import com.qoobot.qooerp.report.dto.ReportUpdateDTO;
import com.qoobot.qooerp.report.service.ReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 报表管理控制器
 *
 * @author Auto
 * @since 2026-02-18
 */
@Slf4j
@RestController
@RequestMapping("/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    /**
     * 创建报表
     */
    @PostMapping
    public Result<ReportDTO> createReport(@Validated @RequestBody ReportCreateDTO dto) {
        log.info("创建报表: {}", dto);
        return Result.success(reportService.createReport(dto));
    }

    /**
     * 更新报表
     */
    @PutMapping("/{id}")
    public Result<Boolean> updateReport(@PathVariable Long id, @Validated @RequestBody ReportUpdateDTO dto) {
        log.info("更新报表: id={}, {}", id, dto);
        dto.setId(id);
        return Result.success(reportService.updateReport(dto));
    }

    /**
     * 删除报表
     */
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteReport(@PathVariable Long id) {
        log.info("删除报表: id={}", id);
        return Result.success(reportService.deleteReport(id));
    }

    /**
     * 获取报表详情
     */
    @GetMapping("/{id}")
    public Result<ReportDTO> getReportById(@PathVariable Long id) {
        log.info("获取报表详情: id={}", id);
        return Result.success(reportService.getReportById(id));
    }

    /**
     * 分页查询报表
     */
    @GetMapping
    public Result<PageResult<ReportDTO>> queryReports(ReportQueryDTO queryDTO) {
        log.info("分页查询报表: {}", queryDTO);
        return Result.success(reportService.queryReports(queryDTO));
    }

    /**
     * 发布报表
     */
    @PostMapping("/{id}/publish")
    public Result<Boolean> publishReport(@PathVariable Long id) {
        log.info("发布报表: id={}", id);
        return Result.success(reportService.publishReport(id));
    }

    /**
     * 归档报表
     */
    @PostMapping("/{id}/archive")
    public Result<Boolean> archiveReport(@PathVariable Long id) {
        log.info("归档报表: id={}", id);
        return Result.success(reportService.archiveReport(id));
    }
}

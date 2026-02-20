package com.qoobot.qooerp.report.controller;

import com.qoobot.qooerp.common.result.Result;
import com.qoobot.qooerp.report.service.ReportDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "报表数据查询", description = "报表数据查询相关接口")
@RestController
@RequestMapping("/api/report/data")
@RequiredArgsConstructor
public class ReportDataController {

    private final ReportDataService reportDataService;

    @Operation(summary = "查询报表数据")
    @PostMapping("/query/{reportId}")
    public Result<Map<String, Object>> queryReportData(
            @PathVariable Long reportId,
            @RequestBody Map<String, Object> params) {
        return Result.success(reportDataService.queryReportData(reportId, params));
    }

    @Operation(summary = "执行SQL查询")
    @PostMapping("/execute/{datasourceId}")
    public Result<Map<String, Object>> executeQuery(
            @PathVariable Long datasourceId,
            @RequestParam String sql,
            @RequestBody(required = false) Map<String, Object> params) {
        return Result.success(reportDataService.executeQuery(datasourceId, sql, params));
    }

    @Operation(summary = "数据下钻")
    @PostMapping("/drilldown/{reportId}")
    public Result<Map<String, Object>> drillDown(
            @PathVariable Long reportId,
            @RequestBody Map<String, Object> params,
            @RequestParam String drillConfig) {
        return Result.success(reportDataService.drillDown(reportId, params, drillConfig));
    }
}

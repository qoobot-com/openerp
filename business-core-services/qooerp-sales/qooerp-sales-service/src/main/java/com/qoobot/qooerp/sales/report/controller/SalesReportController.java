package com.qoobot.qooerp.sales.report.controller;

import com.qoobot.qooerp.common.result.Result;
import com.qoobot.qooerp.sales.report.dto.*;
import com.qoobot.qooerp.sales.report.service.SalesReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 销售报表 Controller
 */
@Tag(name = "销售报表管理", description = "销售报表相关接口")
@RestController
@RequestMapping("/api/sales/reports")
@RequiredArgsConstructor
public class SalesReportController {

    private final SalesReportService reportService;

    @Operation(summary = "获取销售统计")
    @GetMapping("/statistics")
    public Result<SalesStatisticsDTO> getSalesStatistics(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        return Result.success(reportService.getSalesStatistics(startDate, endDate));
    }

    @Operation(summary = "获取销售趋势")
    @GetMapping("/trend")
    public Result<List<SalesTrendDTO>> getSalesTrend(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        return Result.success(reportService.getSalesTrend(startDate, endDate));
    }

    @Operation(summary = "获取商品销售排行")
    @GetMapping("/ranking/products")
    public Result<List<SalesRankingDTO>> getProductRanking(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam(defaultValue = "10") Integer topN) {
        return Result.success(reportService.getProductRanking(startDate, endDate, topN));
    }

    @Operation(summary = "获取客户销售排行")
    @GetMapping("/ranking/customers")
    public Result<List<SalesRankingDTO>> getCustomerRanking(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam(defaultValue = "10") Integer topN) {
        return Result.success(reportService.getCustomerRanking(startDate, endDate, topN));
    }

    @Operation(summary = "获取销售员业绩排行")
    @GetMapping("/ranking/salespersons")
    public Result<List<SalesRankingDTO>> getSalespersonRanking(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam(defaultValue = "10") Integer topN) {
        return Result.success(reportService.getSalespersonRanking(startDate, endDate, topN));
    }
}

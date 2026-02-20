package com.qoobot.qooerp.report.controller;

import com.qoobot.qooerp.report.service.ReportExportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "报表导出", description = "报表导出相关接口")
@RestController
@RequestMapping("/api/report/export")
@RequiredArgsConstructor
public class ReportExportController {

    private final ReportExportService exportService;

    @Operation(summary = "导出为Excel")
    @PostMapping("/excel/{reportId}")
    public ResponseEntity<byte[]> exportToExcel(
            @PathVariable Long reportId,
            @RequestBody(required = false) Map<String, Object> params) {
        return exportService.exportToExcel(reportId, params);
    }

    @Operation(summary = "导出为PDF")
    @PostMapping("/pdf/{reportId}")
    public ResponseEntity<byte[]> exportToPdf(
            @PathVariable Long reportId,
            @RequestBody(required = false) Map<String, Object> params) {
        return exportService.exportToPdf(reportId, params);
    }

    @Operation(summary = "导出为CSV")
    @PostMapping("/csv/{reportId}")
    public ResponseEntity<byte[]> exportToCsv(
            @PathVariable Long reportId,
            @RequestBody(required = false) Map<String, Object> params) {
        return exportService.exportToCsv(reportId, params);
    }

    @Operation(summary = "导出为图片")
    @PostMapping("/image/{reportId}")
    public ResponseEntity<byte[]> exportToImage(
            @PathVariable Long reportId,
            @RequestBody(required = false) Map<String, Object> params) {
        return exportService.exportToImage(reportId, params);
    }
}

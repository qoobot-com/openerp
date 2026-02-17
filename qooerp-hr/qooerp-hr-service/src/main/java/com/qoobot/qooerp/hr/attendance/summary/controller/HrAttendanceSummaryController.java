package com.qoobot.qooerp.hr.attendance.summary.controller;

import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.hr.attendance.summary.domain.HrAttendanceSummary;
import com.qoobot.qooerp.hr.attendance.summary.service.IHrAttendanceSummaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 考勤汇总控制器
 *
 * @author QooERP Team
 * @since 2026-02-17
 */
@Slf4j
@RestController
@RequestMapping("/attendance-summary")
@RequiredArgsConstructor
@Tag(name = "考勤汇总", description = "考勤报表与汇总")
@Validated
public class HrAttendanceSummaryController {

    private final IHrAttendanceSummaryService summaryService;

    @Operation(summary = "生成月度考勤汇总")
    @PostMapping("/generate/{employeeId}/{year}/{month}")
    public Result<HrAttendanceSummary> generateMonthlySummary(
            @PathVariable Long employeeId,
            @PathVariable Integer year,
            @PathVariable Integer month) {
        HrAttendanceSummary summary = summaryService.generateMonthlySummary(employeeId, year, month);
        return Result.success(summary);
    }

    @Operation(summary = "批量生成月度考勤汇总")
    @PostMapping("/batch-generate/{deptId}/{year}/{month}")
    public Result<Boolean> batchGenerateMonthlySummary(
            @PathVariable Long deptId,
            @PathVariable Integer year,
            @PathVariable Integer month) {
        Boolean result = summaryService.batchGenerateMonthlySummary(deptId, year, month);
        return Result.success(result);
    }

    @Operation(summary = "获取员工考勤汇总")
    @GetMapping("/employee/{employeeId}")
    public Result<List<HrAttendanceSummary>> getEmployeeSummary(
            @PathVariable Long employeeId,
            @RequestParam String startDate,
            @RequestParam String endDate) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        List<HrAttendanceSummary> list = summaryService.getEmployeeSummary(employeeId, start, end);
        return Result.success(list);
    }

    @Operation(summary = "获取部门考勤汇总")
    @GetMapping("/dept/{deptId}/{year}/{month}")
    public Result<List<HrAttendanceSummary>> getDeptSummary(
            @PathVariable Long deptId,
            @PathVariable Integer year,
            @PathVariable Integer month) {
        List<HrAttendanceSummary> list = summaryService.getDeptSummary(deptId, year, month);
        return Result.success(list);
    }

    @Operation(summary = "获取考勤汇总详情")
    @GetMapping("/{summaryId}")
    public Result<HrAttendanceSummary> getSummary(@PathVariable Long summaryId) {
        HrAttendanceSummary summary = summaryService.getById(summaryId);
        return Result.success(summary);
    }
}

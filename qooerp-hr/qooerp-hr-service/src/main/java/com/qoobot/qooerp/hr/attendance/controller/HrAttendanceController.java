package com.qoobot.qooerp.hr.attendance.controller;

import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.hr.attendance.domain.HrAttendance;
import com.qoobot.qooerp.hr.attendance.service.IHrAttendanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 考勤管理控制器
 *
 * @author QooERP Team
 * @since 2026-02-17
 */
@Slf4j
@RestController
@RequestMapping("/attendance")
@RequiredArgsConstructor
@Tag(name = "考勤管理", description = "员工考勤打卡管理")
@Validated
public class HrAttendanceController {

    private final IHrAttendanceService attendanceService;

    @Operation(summary = "员工签到")
    @PostMapping("/check-in/{employeeId}")
    public Result<Void> checkIn(@PathVariable Long employeeId) {
        attendanceService.checkIn(employeeId);
        return Result.success();
    }

    @Operation(summary = "员工签退")
    @PostMapping("/check-out/{employeeId}")
    public Result<Void> checkOut(@PathVariable Long employeeId) {
        attendanceService.checkOut(employeeId);
        return Result.success();
    }

    @Operation(summary = "查询考勤记录")
    @GetMapping("/{employeeId}/{date}")
    public Result<HrAttendance> getAttendanceByDate(
            @PathVariable Long employeeId,
            @PathVariable String date) {
        LocalDate attendanceDate = LocalDate.parse(date);
        HrAttendance attendance = attendanceService.getAttendanceByDate(employeeId, attendanceDate);
        return Result.success(attendance);
    }

    @Operation(summary = "查询考勤列表")
    @GetMapping("/list/{employeeId}")
    public Result<List<HrAttendance>> getAttendanceList(
            @PathVariable Long employeeId,
            @RequestParam String startDate,
            @RequestParam String endDate) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        List<HrAttendance> list = attendanceService.getAttendanceList(employeeId, start, end);
        return Result.success(list);
    }
}

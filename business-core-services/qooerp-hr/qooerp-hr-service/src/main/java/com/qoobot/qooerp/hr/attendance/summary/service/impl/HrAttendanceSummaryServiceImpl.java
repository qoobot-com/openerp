package com.qoobot.qooerp.hr.attendance.summary.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qoobot.qooerp.hr.attendance.domain.HrAttendance;
import com.qoobot.qooerp.hr.attendance.summary.domain.HrAttendanceSummary;
import com.qoobot.qooerp.hr.attendance.summary.mapper.HrAttendanceSummaryMapper;
import com.qoobot.qooerp.hr.attendance.summary.service.IHrAttendanceSummaryService;
import com.qoobot.qooerp.hr.attendance.service.IHrAttendanceService;
import com.qoobot.qooerp.hr.business.trip.domain.HrBusinessTrip;
import com.qoobot.qooerp.hr.business.trip.service.IHrBusinessTripService;
import com.qoobot.qooerp.hr.employee.domain.HrEmployee;
import com.qoobot.qooerp.hr.employee.service.IHrEmployeeService;
import com.qoobot.qooerp.hr.leave.domain.HrLeave;
import com.qoobot.qooerp.hr.leave.service.IHrLeaveService;
import com.qoobot.qooerp.hr.overtime.domain.HrOvertime;
import com.qoobot.qooerp.hr.overtime.service.IHrOvertimeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * 考勤汇总服务实现
 *
 * @author QooERP Team
 * @since 20xx-xx-xx
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HrAttendanceSummaryServiceImpl extends ServiceImpl<HrAttendanceSummaryMapper, HrAttendanceSummary>
        implements IHrAttendanceSummaryService {

    private final IHrAttendanceService attendanceService;
    private final IHrOvertimeService overtimeService;
    private final IHrLeaveService leaveService;
    private final IHrBusinessTripService businessTripService;
    private final IHrEmployeeService employeeService;

    @Override
    public HrAttendanceSummary generateMonthlySummary(Long employeeId, Integer year, Integer month) {
        log.info("生成月度考勤汇总: 员工ID={}, 年={}, 月={}", employeeId, year, month);

        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        HrAttendanceSummary summary = new HrAttendanceSummary();
        summary.setEmployeeId(employeeId);

        // 获取员工信息
        HrEmployee employee = employeeService.getEmployeeById(employeeId);
        if (employee != null) {
            summary.setEmployeeName(employee.getEmployeeName());
            summary.setDeptId(employee.getDepartmentId());
        }

        summary.setSummaryMonth(String.format("%d-%02d", year, month));

        // 统计出勤记录
        List<HrAttendance> attendanceList = attendanceService.getAttendanceList(employeeId, startDate, endDate);
        summary.setAttendanceDays(attendanceList.size());

        // 统计工作时长
        Integer totalWorkHours = attendanceList.stream()
                .filter(a -> a.getWorkHours() != null)
                .map(HrAttendance::getWorkHours)
                .reduce(0, Integer::sum);
        summary.setWorkHours(BigDecimal.valueOf(totalWorkHours));

        // 统计迟到、早退、缺卡次数
        // TODO: HrAttendance 实体暂无迟到、早退、缺卡等字段,需要补充后实现统计
        summary.setLateTimes(0);
        summary.setEarlyTimes(0);
        summary.setAbsentTimes(0);

        // 统计请假天数（已审批通过的请假）
        List<HrLeave> leaveList = leaveService.getMyLeave(employeeId);
        BigDecimal totalLeaveDays = BigDecimal.ZERO;
        BigDecimal casualLeaveDays = BigDecimal.ZERO;
        BigDecimal sickLeaveDays = BigDecimal.ZERO;
        BigDecimal annualLeaveDays = BigDecimal.ZERO;

        for (HrLeave leave : leaveList) {
            if (leave.getApprovalStatus() == 1) { // 已通过
                LocalDate leaveStart = leave.getStartDate();
                LocalDate leaveEnd = leave.getEndDate();

                if (!leaveStart.isBefore(startDate) && !leaveEnd.isAfter(endDate)) {
                    totalLeaveDays = totalLeaveDays.add(leave.getLeaveDays());

                    if (leave.getLeaveType() == 1) { // 事假
                        casualLeaveDays = casualLeaveDays.add(leave.getLeaveDays());
                    } else if (leave.getLeaveType() == 2) { // 病假
                        sickLeaveDays = sickLeaveDays.add(leave.getLeaveDays());
                    } else if (leave.getLeaveType() == 3) { // 年假
                        annualLeaveDays = annualLeaveDays.add(leave.getLeaveDays());
                    }
                }
            }
        }

        summary.setLeaveDays(totalLeaveDays);
        summary.setCasualLeaveDays(casualLeaveDays);
        summary.setSickLeaveDays(sickLeaveDays);
        summary.setAnnualLeaveDays(annualLeaveDays);

        // 统计加班天数和时长
        List<HrOvertime> overtimeList = overtimeService.getMyOvertime(employeeId);
        BigDecimal totalOvertimeDays = BigDecimal.ZERO;
        BigDecimal totalOvertimeHours = BigDecimal.ZERO;

        for (HrOvertime overtime : overtimeList) {
            if (overtime.getApprovalStatus() == 1) { // 已通过
                LocalDate overtimeDate = overtime.getStartTime().toLocalDate();
                if (!overtimeDate.isBefore(startDate) && !overtimeDate.isAfter(endDate)) {
                    if (overtime.getDuration() != null) {
                        BigDecimal duration = BigDecimal.valueOf(overtime.getDuration());
                        totalOvertimeHours = totalOvertimeHours.add(duration);
                        totalOvertimeDays = totalOvertimeDays.add(
                                duration.divide(BigDecimal.valueOf(8), 1, RoundingMode.HALF_UP));
                    }
                }
            }
        }

        summary.setOvertimeDays(totalOvertimeDays);
        summary.setOvertimeHours(totalOvertimeHours);

        // 统计出差天数
        List<HrBusinessTrip> tripList = businessTripService.getMyBusinessTrips(employeeId);
        BigDecimal totalTripDays = BigDecimal.ZERO;

        for (HrBusinessTrip trip : tripList) {
            if (trip.getApprovalStatus() == 1) { // 已通过
                if (!trip.getStartDate().isBefore(startDate) && !trip.getEndDate().isAfter(endDate)) {
                    if (trip.getDays() != null) {
                        totalTripDays = totalTripDays.add(BigDecimal.valueOf(trip.getDays()));
                    }
                }
            }
        }

        summary.setBusinessTripDays(totalTripDays);

        this.save(summary);
        return summary;
    }

    @Override
    public Boolean batchGenerateMonthlySummary(Long deptId, Integer year, Integer month) {
        log.info("批量生成月度考勤汇总: 部门ID={}, 年={}, 月={}", deptId, year, month);

        // 获取部门下所有员工
        LambdaQueryWrapper<HrEmployee> employeeWrapper = new LambdaQueryWrapper<>();
        employeeWrapper.eq(HrEmployee::getDepartmentId, deptId);
        List<HrEmployee> employeeList = employeeService.list(employeeWrapper);

        if (employeeList.isEmpty()) {
            log.warn("部门下无员工: deptId={}", deptId);
            return true;
        }

        // 批量为每个员工生成考勤汇总
        for (HrEmployee employee : employeeList) {
            try {
                generateMonthlySummary(employee.getId(), year, month);
            } catch (Exception e) {
                log.error("生成员工考勤汇总失败: employeeId={}, error={}", employee.getId(), e.getMessage(), e);
            }
        }

        log.info("批量生成月度考勤汇总完成: 部门ID={}, 员工数={}", deptId, employeeList.size());
        return true;
    }

    @Override
    public List<HrAttendanceSummary> getEmployeeSummary(Long employeeId, LocalDate startDate, LocalDate endDate) {
        LambdaQueryWrapper<HrAttendanceSummary> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HrAttendanceSummary::getEmployeeId, employeeId);
        wrapper.ge(HrAttendanceSummary::getSummaryMonth, startDate.toString().substring(0, 7));
        wrapper.le(HrAttendanceSummary::getSummaryMonth, endDate.toString().substring(0, 7));
        wrapper.orderByDesc(HrAttendanceSummary::getSummaryMonth);
        return this.list(wrapper);
    }

    @Override
    public List<HrAttendanceSummary> getDeptSummary(Long deptId, Integer year, Integer month) {
        LambdaQueryWrapper<HrAttendanceSummary> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HrAttendanceSummary::getDeptId, deptId);
        wrapper.eq(HrAttendanceSummary::getSummaryMonth, String.format("%d-%02d", year, month));
        wrapper.orderByDesc(HrAttendanceSummary::getSummaryMonth);
        return this.list(wrapper);
    }
}

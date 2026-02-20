package com.qoobot.qooerp.hr.attendance.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qoobot.qooerp.hr.attendance.domain.HrAttendance;
import com.qoobot.qooerp.hr.attendance.mapper.HrAttendanceMapper;
import com.qoobot.qooerp.hr.attendance.service.IHrAttendanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 考勤管理服务实现
 *
 * @author QooERP Team
 * @since 20xx-xx-xx
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HrAttendanceServiceImpl extends ServiceImpl<HrAttendanceMapper, HrAttendance>
        implements IHrAttendanceService {

    private final HrAttendanceMapper attendanceMapper;

    @Override
    public void checkIn(Long employeeId) {
        LocalDate today = LocalDate.now();
        HrAttendance attendance = getAttendanceByDate(employeeId, today);
        
        if (attendance == null) {
            // 创建新的考勤记录
            attendance = new HrAttendance();
            attendance.setEmployeeId(employeeId);
            attendance.setAttendanceDate(today);
            attendance.setCheckInTime(LocalDateTime.now());
            attendance.setStatus("1"); // 正常
            attendanceMapper.insert(attendance);
        } else {
            // 更新签到时间
            attendance.setCheckInTime(LocalDateTime.now());
            attendanceMapper.updateById(attendance);
        }
        
        log.info("员工签到成功: employeeId={}", employeeId);
    }

    @Override
    public void checkOut(Long employeeId) {
        LocalDate today = LocalDate.now();
        HrAttendance attendance = getAttendanceByDate(employeeId, today);
        
        if (attendance == null) {
            throw new RuntimeException("请先签到");
        }
        
        attendance.setCheckOutTime(LocalDateTime.now());
        attendanceMapper.updateById(attendance);
        log.info("员工签退成功: employeeId={}", employeeId);
    }

    @Override
    public HrAttendance getAttendanceByDate(Long employeeId, LocalDate date) {
        return attendanceMapper.selectOne(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<HrAttendance>()
                .eq(HrAttendance::getEmployeeId, employeeId)
                .eq(HrAttendance::getAttendanceDate, date)
        );
    }

    @Override
    public List<HrAttendance> getAttendanceList(Long employeeId, LocalDate startDate, LocalDate endDate) {
        return attendanceMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<HrAttendance>()
                .eq(HrAttendance::getEmployeeId, employeeId)
                .between(HrAttendance::getAttendanceDate, startDate, endDate)
                .orderByDesc(HrAttendance::getAttendanceDate)
        );
    }
}

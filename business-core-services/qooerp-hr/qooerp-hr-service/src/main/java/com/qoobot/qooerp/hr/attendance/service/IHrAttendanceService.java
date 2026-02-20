package com.qoobot.qooerp.hr.attendance.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qoobot.qooerp.hr.attendance.domain.HrAttendance;

import java.time.LocalDate;
import java.util.List;

/**
 * 考勤管理服务接口
 *
 * @author QooERP Team
 * @since 2026-02-17
 */
public interface IHrAttendanceService extends IService<HrAttendance> {

    /**
     * 签到
     */
    void checkIn(Long employeeId);

    /**
     * 签退
     */
    void checkOut(Long employeeId);

    /**
     * 获取员工考勤记录
     */
    HrAttendance getAttendanceByDate(Long employeeId, LocalDate date);

    /**
     * 获取员工考勤列表
     */
    List<HrAttendance> getAttendanceList(Long employeeId, LocalDate startDate, LocalDate endDate);
}

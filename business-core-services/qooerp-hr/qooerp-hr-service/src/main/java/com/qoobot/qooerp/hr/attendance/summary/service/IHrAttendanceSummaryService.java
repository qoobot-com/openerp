package com.qoobot.qooerp.hr.attendance.summary.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qoobot.qooerp.hr.attendance.summary.domain.HrAttendanceSummary;

import java.time.LocalDate;
import java.util.List;

/**
 * 考勤汇总服务接口
 *
 * @author QooERP Team
 * @since 2026-02-17
 */
public interface IHrAttendanceSummaryService extends IService<HrAttendanceSummary> {

    /**
     * 生成月度考勤汇总
     *
     * @param employeeId 员工ID
     * @param year 年份
     * @param month 月份
     * @return 汇总记录
     */
    HrAttendanceSummary generateMonthlySummary(Long employeeId, Integer year, Integer month);

    /**
     * 批量生成月度考勤汇总
     *
     * @param deptId 部门ID
     * @param year 年份
     * @param month 月份
     * @return 是否成功
     */
    Boolean batchGenerateMonthlySummary(Long deptId, Integer year, Integer month);

    /**
     * 获取员工考勤汇总
     *
     * @param employeeId 员工ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 汇总列表
     */
    List<HrAttendanceSummary> getEmployeeSummary(Long employeeId, LocalDate startDate, LocalDate endDate);

    /**
     * 获取部门考勤汇总
     *
     * @param deptId 部门ID
     * @param year 年份
     * @param month 月份
     * @return 汇总列表
     */
    List<HrAttendanceSummary> getDeptSummary(Long deptId, Integer year, Integer month);
}

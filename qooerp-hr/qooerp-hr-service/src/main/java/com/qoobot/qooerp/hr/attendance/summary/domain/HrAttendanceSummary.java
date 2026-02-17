package com.qoobot.qooerp.hr.attendance.summary.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qoobot.qooerp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 考勤汇总实体
 *
 * @author QooERP Team
 * @since 2026-02-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("hr_attendance_summary")
public class HrAttendanceSummary extends BaseEntity {

    /**
     * 汇总ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 员工ID
     */
    @TableField("employee_id")
    private Long employeeId;

    /**
     * 员工姓名
     */
    @TableField("employee_name")
    private String employeeName;

    /**
     * 部门ID
     */
    @TableField("dept_id")
    private Long deptId;

    /**
     * 部门名称
     */
    @TableField("dept_name")
    private String deptName;

    /**
     * 汇总年月（格式：yyyy-MM）
     */
    @TableField("summary_month")
    private String summaryMonth;

    /**
     * 出勤天数
     */
    @TableField("attendance_days")
    private Integer attendanceDays;

    /**
     * 请假天数
     */
    @TableField("leave_days")
    private BigDecimal leaveDays;

    /**
     * 事假天数
     */
    @TableField("casual_leave_days")
    private BigDecimal casualLeaveDays;

    /**
     * 病假天数
     */
    @TableField("sick_leave_days")
    private BigDecimal sickLeaveDays;

    /**
     * 年假天数
     */
    @TableField("annual_leave_days")
    private BigDecimal annualLeaveDays;

    /**
     * 加班天数
     */
    @TableField("overtime_days")
    private BigDecimal overtimeDays;

    /**
     * 加班时长（小时）
     */
    @TableField("overtime_hours")
    private BigDecimal overtimeHours;

    /**
     * 出差天数
     */
    @TableField("business_trip_days")
    private BigDecimal businessTripDays;

    /**
     * 迟到次数
     */
    @TableField("late_times")
    private Integer lateTimes;

    /**
     * 早退次数
     */
    @TableField("early_times")
    private Integer earlyTimes;

    /**
     * 缺卡次数
     */
    @TableField("absent_times")
    private Integer absentTimes;

    /**
     * 工作时长（小时）
     */
    @TableField("work_hours")
    private BigDecimal workHours;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;
}

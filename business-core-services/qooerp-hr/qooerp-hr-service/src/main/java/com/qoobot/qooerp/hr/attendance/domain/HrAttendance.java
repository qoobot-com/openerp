package com.qoobot.qooerp.hr.attendance.domain;

import com.qoobot.qooerp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 考勤记录实体
 *
 * @author QooERP Team
 * @since 2026-02-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class HrAttendance extends BaseEntity {

    /** 考勤编号 */
    private String attendanceCode;

    /** 员工ID */
    private Long employeeId;

    /** 考勤日期 */
    private LocalDate attendanceDate;

    /** 打卡时间 */
    private LocalDateTime checkInTime;

    /** 打卡时间 */
    private LocalDateTime checkOutTime;

    /** 工作时长（小时） */
    private Integer workHours;

    /** 加班时长（小时） */
    private Integer overtimeHours;

    /** 请假类型 */
    private String leaveType;

    /** 请假时长（小时） */
    private Integer leaveHours;

    /** 出差天数 */
    private Integer travelDays;

    /** 考勤状态 */
    private String status;

    /** 备注 */
    private String remark;
}

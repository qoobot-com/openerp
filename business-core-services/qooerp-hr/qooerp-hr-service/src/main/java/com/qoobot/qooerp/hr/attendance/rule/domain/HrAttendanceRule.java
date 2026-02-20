package com.qoobot.qooerp.hr.attendance.rule.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalTime;

/**
 * 考勤规则实体
 */
@Data
@TableName("hr_attendance_rule")
public class HrAttendanceRule {

    /**
     * 规则ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 规则名称
     */
    private String ruleName;

    /**
     * 规则编码
     */
    private String ruleCode;

    /**
     * 适用部门ID（为空表示全局）
     */
    private Long departmentId;

    /**
     * 上班时间
     */
    private LocalTime workStartTime;

    /**
     * 下班时间
     */
    private LocalTime workEndTime;

    /**
     * 迟到分钟数
     */
    private Integer lateMinutes;

    /**
     * 早退分钟数
     */
    private Integer earlyLeaveMinutes;

    /**
     * 允许迟到分钟数
     */
    private Integer allowLateMinutes;

    /**
     * 允许早退分钟数
     */
    private Integer allowEarlyMinutes;

    /**
     * 缺勤判定小时数
     */
    private Double absentHours;

    /**
     * 是否启用弹性工作制
     */
    private Boolean flexibleWork;

    /**
     * 弹性工作最早打卡时间
     */
    private LocalTime flexibleEarlyTime;

    /**
     * 弹性工作最晚打卡时间
     */
    private LocalTime flexibleLateTime;

    /**
     * 工作时长要求（小时）
     */
    private Double workHoursRequired;

    /**
     * 是否启用
     */
    private Boolean enabled;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 创建时间
     */
    private java.time.LocalDateTime createTime;

    /**
     * 更新人
     */
    private String updateBy;

    /**
     * 更新时间
     */
    private java.time.LocalDateTime updateTime;

    /**
     * 租户ID
     */
    private Long tenantId;
}

package com.qoobot.qooerp.hr.schedule.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qoobot.qooerp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 排班计划实体
 *
 * @author QooERP Team
 * @since 2026-02-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("hr_schedule_plan")
public class HrSchedulePlan extends BaseEntity {

    /**
     * 计划ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 计划名称
     */
    @TableField("plan_name")
    private String planName;

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
     * 班次ID
     */
    @TableField("shift_id")
    private Long shiftId;

    /**
     * 班次名称
     */
    @TableField("shift_name")
    private String shiftName;

    /**
     * 计划日期
     */
    @TableField("plan_date")
    private LocalDate planDate;

    /**
     * 星期：1-周一 2-周二 3-周三 4-周四 5-周五 6-周六 7-周日
     */
    @TableField("day_of_week")
    private Integer dayOfWeek;

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
     * 备注
     */
    @TableField("remark")
    private String remark;

    /**
     * 状态：0-禁用 1-启用
     */
    @TableField("status")
    private Integer status;
}

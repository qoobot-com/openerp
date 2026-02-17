package com.qoobot.qooerp.hr.schedule.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qoobot.qooerp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 班次实体
 *
 * @author QooERP Team
 * @since 2026-02-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("hr_shift")
public class HrShift extends BaseEntity {

    /**
     * 班次ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 班次名称
     */
    @TableField("shift_name")
    private String shiftName;

    /**
     * 班次编码
     */
    @TableField("shift_code")
    private String shiftCode;

    /**
     * 上班时间
     */
    @TableField("start_time")
    private String startTime;

    /**
     * 下班时间
     */
    @TableField("end_time")
    private String endTime;

    /**
     * 午休开始时间
     */
    @TableField("lunch_start")
    private String lunchStart;

    /**
     * 午休结束时间
     */
    @TableField("lunch_end")
    private String lunchEnd;

    /**
     * 工作时长（小时）
     */
    @TableField("work_hours")
    private Double workHours;

    /**
     * 允许迟到分钟数
     */
    @TableField("late_tolerance")
    private Integer lateTolerance;

    /**
     * 允许早退分钟数
     */
    @TableField("early_tolerance")
    private Integer earlyTolerance;

    /**
     * 跨天：0-否 1-是
     */
    @TableField("cross_day")
    private Integer crossDay;

    /**
     * 排序
     */
    @TableField("sort_order")
    private Integer sortOrder;

    /**
     * 描述
     */
    @TableField("description")
    private String description;

    /**
     * 状态：0-禁用 1-启用
     */
    @TableField("status")
    private Integer status;
}

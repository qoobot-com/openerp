package com.qoobot.qooerp.hr.schedule.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qoobot.qooerp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 排班规则实体
 *
 * @author QooERP Team
 * @since 2026-02-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("hr_schedule_rule")
public class HrScheduleRule extends BaseEntity {

    /**
     * 规则ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 规则名称
     */
    @TableField("rule_name")
    private String ruleName;

    /**
     * 规则类型：1-固定班次 2-弹性班次 3-自定义
     */
    @TableField("rule_type")
    private Integer ruleType;

    /**
     * 班次ID
     */
    @TableField("shift_id")
    private Long shiftId;

    /**
     * 工作日：1-周一 2-周二 3-周三 4-周四 5-周五 6-周六 7-周日
     * 多选：多个值逗号分隔，如：1,2,3,4,5
     */
    @TableField("work_days")
    private String workDays;

    /**
     * 弹性最早上班时间
     */
    @TableField("flexible_start")
    private String flexibleStart;

    /**
     * 弹性最晚下班时间
     */
    @TableField("flexible_end")
    private String flexibleEnd;

    /**
     * 最短工时（小时）
     */
    @TableField("min_work_hours")
    private Double minWorkHours;

    /**
     * 最长工时（小时）
     */
    @TableField("max_work_hours")
    private Double maxWorkHours;

    /**
     * 适用部门ID（空表示全部）
     */
    @TableField("dept_id")
    private Long deptId;

    /**
     * 适用岗位ID（空表示全部）
     */
    @TableField("position_id")
    private Long positionId;

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

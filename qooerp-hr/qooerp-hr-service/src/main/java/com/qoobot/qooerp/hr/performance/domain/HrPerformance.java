package com.qoobot.qooerp.hr.performance.domain;

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
 * 绩效评估实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("hr_performance")
public class HrPerformance extends BaseEntity {

    /**
     * 主键ID
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
     * 评估编号
     */
    @TableField("performance_no")
    private String performanceNo;

    /**
     * 绩效周期
     */
    @TableField("cycle_id")
    private Long cycleId;

    /**
     * 绩效周期名称
     */
    @TableField("cycle_name")
    private String cycleName;

    /**
     * 评估类型：1-月度，2-季度，3-半年度，4-年度
     */
    @TableField("assess_type")
    private Integer assessType;

    /**
     * 评估年份
     */
    @TableField("assess_year")
    private Integer assessYear;

    /**
     * 评估月份
     */
    @TableField("assess_month")
    private Integer assessMonth;

    /**
     * KPI得分
     */
    @TableField("kpi_score")
    private BigDecimal kpiScore;

    /**
     * KPI权重
     */
    @TableField("kpi_weight")
    private BigDecimal kpiWeight;

    /**
     * 目标完成度
     */
    @TableField("goal_completion")
    private BigDecimal goalCompletion;

    /**
     * 工作态度得分
     */
    @TableField("attitude_score")
    private BigDecimal attitudeScore;

    /**
     * 工作态度权重
     */
    @TableField("attitude_weight")
    private BigDecimal attitudeWeight;

    /**
     * 能力素质得分
     */
    @TableField("ability_score")
    private BigDecimal abilityScore;

    /**
     * 能力素质权重
     */
    @TableField("ability_weight")
    private BigDecimal abilityWeight;

    /**
     * 360度评分（平均分）
     */
    @TableField("degree360_score")
    private BigDecimal degree360Score;

    /**
     * 360度权重
     */
    @TableField("degree360_weight")
    private BigDecimal degree360Weight;

    /**
     * 综合得分
     */
    @TableField("total_score")
    private BigDecimal totalScore;

    /**
     * 绩效等级：A-优秀，B-良好，C-合格，D-需改进
     */
    @TableField("performance_level")
    private String performanceLevel;

    /**
     * 评估人ID
     */
    @TableField("assessor_id")
    private Long assessorId;

    /**
     * 评估人姓名
     */
    @TableField("assessor_name")
    private String assessorName;

    /**
     * 评估日期
     */
    @TableField("assess_date")
    private LocalDate assessDate;

    /**
     * 自评得分
     */
    @TableField("self_score")
    private BigDecimal selfScore;

    /**
     * 自评评语
     */
    @TableField("self_comment")
    private String selfComment;

    /**
     * 主管评语
     */
    @TableField("supervisor_comment")
    private String supervisorComment;

    /**
     * 改进建议
     */
    @TableField("improvement_plan")
    private String improvementPlan;

    /**
     * 状态：0-待评估，1-自评中，2-主管评估中，3-已评估，4-已复核
     */
    @TableField("status")
    private Integer status;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;
}

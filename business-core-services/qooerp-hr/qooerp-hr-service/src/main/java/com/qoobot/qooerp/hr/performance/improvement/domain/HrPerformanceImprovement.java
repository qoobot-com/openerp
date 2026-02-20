package com.qoobot.qooerp.hr.performance.improvement.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDate;

/**
 * 绩效改进实体
 */
@Data
@TableName("hr_performance_improvement")
public class HrPerformanceImprovement {

    /**
     * 改进计划ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 绩效评估ID
     */
    private Long performanceId;

    /**
     * 员工ID
     */
    private Long employeeId;

    /**
     * 改进计划名称
     */
    private String planName;

    /**
     * 改进类型
     */
    private String improvementType; // SKILL-技能提升, PERFORMANCE-绩效改进, ATTITUDE-态度改进, BEHAVIOR-行为改进

    /**
     * 改进目标
     */
    private String improvementGoal;

    /**
     * 计划状态
     */
    private String status; // DRAFT-草稿, IN_PROGRESS-进行中, REVIEWING-审核中, COMPLETED-已完成, CANCELLED-已取消

    /**
     * 开始日期
     */
    private LocalDate startDate;

    /**
     * 结束日期
     */
    private LocalDate endDate;

    /**
     * 审核人
     */
    private String reviewer;

    /**
     * 审核人ID
     */
    private Long reviewerId;

    /**
     * 审核日期
     */
    private LocalDate reviewDate;

    /**
     * 审核意见
     */
    private String reviewComment;

    /**
     * 改进措施
     */
    private String improvementMeasures;

    /**
     * 资源支持
     */
    private String resources;

    /**
     * 培训计划
     */
    private String trainingPlan;

    /**
     * 完成情况
     */
    private String completionStatus;

    /**
     * 完成百分比
     */
    private Double completionPercent;

    /**
     * 改进效果
     */
    private String improvementEffect;

    /**
     * 改进评分
     */
    private Double improvementScore;

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

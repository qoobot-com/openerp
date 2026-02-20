package com.qoobot.qooerp.hr.performance.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qoobot.qooerp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 360度评估实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("hr_degree360_evaluation")
public class HrDegree360Evaluation extends BaseEntity {

    /**
     * 主键ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 绩效评估ID
     */
    @TableField("performance_id")
    private Long performanceId;

    /**
     * 被评估人ID
     */
    @TableField("employee_id")
    private Long employeeId;

    /**
     * 评估人ID
     */
    @TableField("evaluator_id")
    private Long evaluatorId;

    /**
     * 评估人姓名
     */
    @TableField("evaluator_name")
    private String evaluatorName;

    /**
     * 评估关系：1-上级，2-同事，3-下属，4-自评
     */
    @TableField("relation_type")
    private Integer relationType;

    /**
     * 工作能力得分
     */
    @TableField("ability_score")
    private BigDecimal abilityScore;

    /**
     * 工作态度得分
     */
    @TableField("attitude_score")
    private BigDecimal attitudeScore;

    /**
     * 团队合作得分
     */
    @TableField("teamwork_score")
    private BigDecimal teamworkScore;

    /**
     * 沟通能力得分
     */
    @TableField("communication_score")
    private BigDecimal communicationScore;

    /**
     * 创新能力得分
     */
    @TableField("innovation_score")
    private BigDecimal innovationScore;

    /**
     * 执行力得分
     */
    @TableField("execution_score")
    private BigDecimal executionScore;

    /**
     * 综合得分
     */
    @TableField("total_score")
    private BigDecimal totalScore;

    /**
     * 评价内容
     */
    @TableField("comment")
    private String comment;

    /**
     * 是否匿名：0-实名，1-匿名
     */
    @TableField("is_anonymous")
    private Boolean isAnonymous;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;
}

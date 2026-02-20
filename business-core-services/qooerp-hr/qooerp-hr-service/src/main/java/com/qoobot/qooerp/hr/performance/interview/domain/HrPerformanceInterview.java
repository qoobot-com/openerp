package com.qoobot.qooerp.hr.performance.interview.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDate;

/**
 * 绩效面谈实体
 */
@Data
@TableName("hr_performance_interview")
public class HrPerformanceInterview {

    /**
     * 面谈ID
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
     * 面谈类型
     */
    private String interviewType; // FINAL-期末面谈, PROGRESS-中期面谈, FEEDBACK-反馈面谈

    /**
     * 面谈日期
     */
    private LocalDate interviewDate;

    /**
     * 面谈地点
     */
    private String location;

    /**
     * 面谈人
     */
    private String interviewer;

    /**
     * 面谈人ID
     */
    private Long interviewerId;

    /**
     * 面谈状态
     */
    private String status; // SCHEDULED-已安排, COMPLETED-已完成, CANCELLED-已取消

    /**
     * 工作亮点
     */
    private String highlights;

    /**
     * 工作不足
     */
    private String weaknesses;

    /**
     * 改进建议
     */
    private String suggestions;

    /**
     * 员工反馈
     */
    private String employeeFeedback;

    /**
     * 培训需求
     */
    private String trainingNeeds;

    /**
     * 发展规划
     */
    private String developmentPlan;

    /**
     * 面谈评分
     */
    private Double interviewScore;

    /**
     * 下次面谈日期
     */
    private LocalDate nextInterviewDate;

    /**
     * 员工签字确认
     */
    private Boolean employeeSigned;

    /**
     * 员工签字日期
     */
    private LocalDate signDate;

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

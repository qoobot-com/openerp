package com.qoobot.qooerp.hr.lifecycle.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDate;

/**
 * 入职流程实体
 */
@Data
@TableName("hr_onboarding")
public class HrOnboarding {

    /**
     * 入职ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 员工ID
     */
    private Long employeeId;

    /**
     * 入职单号
     */
    private String onboardingNo;

    /**
     * 入职状态
     */
    private String status; // DRAFT-草稿, PENDING-待入职, PROCESSING-入职中, COMPLETED-已完成, CANCELLED-已取消

    /**
     * Offer状态
     */
    private String offerStatus; // NOT_SENT-未发送, SENT-已发送, ACCEPTED-已接受, REJECTED-已拒绝

    /**
     * Offer发放日期
     */
    private LocalDate offerDate;

    /**
     * Offer接受日期
     */
    private LocalDate acceptDate;

    /**
     * 背景调查状态
     */
    private String bgCheckStatus; // NOT_REQUIRED-不需要, PENDING-待调查, PROCESSING-调查中, PASSED-通过, FAILED-不通过

    /**
     * 背景调查报告
     */
    private String bgCheckReport;

    /**
     * 入职日期
     */
    private LocalDate onboardDate;

    /**
     * 试用期开始日期
     */
    private LocalDate probationStartDate;

    /**
     * 试用期结束日期
     */
    private LocalDate probationEndDate;

    /**
     * 岗位ID
     */
    private Long positionId;

    /**
     * 部门ID
     */
    private Long departmentId;

    /**
     * 基本工资
     */
    private Double baseSalary;

    /**
     * 薪酬结构ID
     */
    private Long salaryStructureId;

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

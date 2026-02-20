package com.qoobot.qooerp.hr.salary.domain;

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
 * 薪酬调整实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("hr_salary_adjust")
public class HrSalaryAdjust extends BaseEntity {

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
     * 调整单号
     */
    @TableField("adjust_no")
    private String adjustNo;

    /**
     * 调整类型：1-调岗调整，2-晋升调整，3-降职调整，4-普调，5-其他
     */
    @TableField("adjust_type")
    private Integer adjustType;

    /**
     * 调整前基本工资
     */
    @TableField("before_base_salary")
    private BigDecimal beforeBaseSalary;

    /**
     * 调整后基本工资
     */
    @TableField("after_base_salary")
    private BigDecimal afterBaseSalary;

    /**
     * 调整前岗位工资
     */
    @TableField("before_post_salary")
    private BigDecimal beforePostSalary;

    /**
     * 调整后岗位工资
     */
    @TableField("after_post_salary")
    private BigDecimal afterPostSalary;

    /**
     * 调整前绩效工资
     */
    @TableField("before_performance_salary")
    private BigDecimal beforePerformanceSalary;

    /**
     * 调整后绩效工资
     */
    @TableField("after_performance_salary")
    private BigDecimal afterPerformanceSalary;

    /**
     * 工资变动额
     */
    @TableField("salary_change")
    private BigDecimal salaryChange;

    /**
     * 生效日期
     */
    @TableField("effective_date")
    private LocalDate effectiveDate;

    /**
     * 调整原因
     */
    @TableField("adjust_reason")
    private String adjustReason;

    /**
     * 审批人ID
     */
    @TableField("approver_id")
    private Long approverId;

    /**
     * 审批人姓名
     */
    @TableField("approver_name")
    private String approverName;

    /**
     * 审批日期
     */
    @TableField("approve_date")
    private LocalDate approveDate;

    /**
     * 状态：0-待审批，1-已通过，2-已拒绝，3-已取消
     */
    @TableField("status")
    private Integer status;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;
}

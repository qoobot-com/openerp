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
 * 薪资发放实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("hr_salary")
public class HrSalary extends BaseEntity {

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
     * 薪资单号
     */
    @TableField("salary_no")
    private String salaryNo;

    /**
     * 薪酬年月
     */
    @TableField("salary_month")
    private String salaryMonth;

    /**
     * 薪酬方案ID
     */
    @TableField("structure_id")
    private Long structureId;

    /**
     * 基本工资
     */
    @TableField("base_salary")
    private BigDecimal baseSalary;

    /**
     * 岗位工资
     */
    @TableField("post_salary")
    private BigDecimal postSalary;

    /**
     * 绩效工资
     */
    @TableField("performance_salary")
    private BigDecimal performanceSalary;

    /**
     * 工龄工资
     */
    @TableField("seniority_salary")
    private BigDecimal senioritySalary;

    /**
     * 岗位津贴
     */
    @TableField("post_allowance")
    private BigDecimal postAllowance;

    /**
     * 交通补贴
     */
    @TableField("transport_allowance")
    private BigDecimal transportAllowance;

    /**
     * 通讯补贴
     */
    @TableField("communication_allowance")
    private BigDecimal communicationAllowance;

    /**
     * 餐饮补贴
     */
    @TableField("meal_allowance")
    private BigDecimal mealAllowance;

    /**
     * 住房补贴
     */
    @TableField("housing_allowance")
    private BigDecimal housingAllowance;

    /**
     * 其他补贴
     */
    @TableField("other_allowance")
    private BigDecimal otherAllowance;

    /**
     * 加班费
     */
    @TableField("overtime_pay")
    private BigDecimal overtimePay;

    /**
     * 奖金
     */
    @TableField("bonus")
    private BigDecimal bonus;

    /**
     * 津贴合计
     */
    @TableField("total_allowance")
    private BigDecimal totalAllowance;

    /**
     * 应发工资
     */
    @TableField("gross_salary")
    private BigDecimal grossSalary;

    /**
     * 社保个人缴纳
     */
    @TableField("social_personal")
    private BigDecimal socialPersonal;

    /**
     * 公积金个人缴纳
     */
    @TableField("fund_personal")
    private BigDecimal fundPersonal;

    /**
     * 个人所得税
     */
    @TableField("personal_tax")
    private BigDecimal personalTax;

    /**
     * 考勤扣款
     */
    @TableField("attendance_deduction")
    private BigDecimal attendanceDeduction;

    /**
     * 其他扣款
     */
    @TableField("other_deduction")
    private BigDecimal otherDeduction;

    /**
     * 扣款合计
     */
    @TableField("total_deduction")
    private BigDecimal totalDeduction;

    /**
     * 实发工资
     */
    @TableField("net_salary")
    private BigDecimal netSalary;

    /**
     * 工作天数
     */
    @TableField("work_days")
    private Integer workDays;

    /**
     * 出勤天数
     */
    @TableField("attendance_days")
    private Integer attendanceDays;

    /**
     * 请假天数
     */
    @TableField("leave_days")
    private BigDecimal leaveDays;

    /**
     * 加班时长（小时）
     */
    @TableField("overtime_hours")
    private BigDecimal overtimeHours;

    /**
     * 状态：0-草稿，1-已核算，2-已发放，3-已作废
     */
    @TableField("status")
    private Integer status;

    /**
     * 发放日期
     */
    @TableField("pay_date")
    private LocalDate payDate;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;
}

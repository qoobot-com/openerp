package com.qoobot.qooerp.hr.salary.analysis.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;

/**
 * 薪酬分析实体
 */
@Data
@TableName("hr_salary_analysis")
public class HrSalaryAnalysis {

    /**
     * 分析ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 分析名称
     */
    private String analysisName;

    /**
     * 分析类型
     */
    private String analysisType; // DEPARTMENT-部门, POSITION-岗位, INDIVIDUAL-个人, PERIOD-周期

    /**
     * 部门ID
     */
    private Long departmentId;

    /**
     * 岗位ID
     */
    private Long positionId;

    /**
     * 员工ID
     */
    private Long employeeId;

    /**
     * 年份
     */
    private Integer year;

    /**
     * 月份
     */
    private Integer month;

    /**
     * 最低工资
     */
    private BigDecimal minSalary;

    /**
     * 最高工资
     */
    private BigDecimal maxSalary;

    /**
     * 平均工资
     */
    private BigDecimal avgSalary;

    /**
     * 中位数工资
     */
    private BigDecimal medianSalary;

    /**
     * 工资标准差
     */
    private BigDecimal stdDeviation;

    /**
     * 工资总额
     */
    private BigDecimal totalSalary;

    /**
     * 基本工资总额
     */
    private BigDecimal totalBaseSalary;

    /**
     * 绩效工资总额
     */
    private BigDecimal totalPerformanceSalary;

    /**
     * 奖金津贴总额
     */
    private BigDecimal totalBonus;

    /**
     * 社保公积金总额
     */
    private BigDecimal totalSocialSecurity;

    /**
     * 个税总额
     */
    private BigDecimal totalTax;

    /**
     * 实发工资总额
     */
    private BigDecimal totalNetSalary;

    /**
     * 人数
     */
    private Integer headcount;

    /**
     * 分析日期
     */
    private java.time.LocalDate analysisDate;

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
     * 租户ID
     */
    private Long tenantId;
}

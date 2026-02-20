package com.qoobot.qooerp.hr.salary.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qoobot.qooerp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 薪酬结构实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("hr_salary_structure")
public class HrSalaryStructure extends BaseEntity {

    /**
     * 主键ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 薪酬方案名称
     */
    @TableField("structure_name")
    private String structureName;

    /**
     * 薪酬方案编码
     */
    @TableField("structure_code")
    private String structureCode;

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
     * 社保个人缴纳比例
     */
    @TableField("social_personal_rate")
    private BigDecimal socialPersonalRate;

    /**
     * 社保公司缴纳比例
     */
    @TableField("social_company_rate")
    private BigDecimal socialCompanyRate;

    /**
     * 公积金个人缴纳比例
     */
    @TableField("fund_personal_rate")
    private BigDecimal fundPersonalRate;

    /**
     * 公积金公司缴纳比例
     */
    @TableField("fund_company_rate")
    private BigDecimal fundCompanyRate;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

    /**
     * 是否启用：0-禁用，1-启用
     */
    @TableField("is_enabled")
    private Boolean isEnabled;

    /**
     * 部门ID（可选，为空表示通用）
     */
    @TableField("department_id")
    private Long departmentId;

    /**
     * 岗位ID（可选，为空表示通用）
     */
    @TableField("position_id")
    private Long positionId;
}

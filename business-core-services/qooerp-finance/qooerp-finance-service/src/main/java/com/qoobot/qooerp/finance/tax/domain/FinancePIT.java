package com.qoobot.qooerp.finance.tax.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 个人所得税实体
 * 
 * @author QooERP Team
 * @since 2026-02-17
 */
@Data
@TableName("finance_pit")
public class FinancePIT {

    /**
     * 个人所得税ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 税务年度
     */
    private Integer taxYear;

    /**
     * 月份
     */
    private Integer month;

    /**
     * 员工ID
     */
    private Long employeeId;

    /**
     * 员工工号
     */
    private String employeeNo;

    /**
     * 员工姓名
     */
    private String employeeName;

    /**
     * 身份证号
     */
    private String idCardNo;

    /**
     * 收入类型（SALARY-工资 BONUS-奖金 STOCK-股权 OTHER-其他）
     */
    private String incomeType;

    /**
     * 应发工资
     */
    private BigDecimal grossSalary;

    /**
     * 社保个人部分
     */
    private BigDecimal socialInsurance;

    /**
     * 公积金个人部分
     */
    private BigDecimal housingFund;

    /**
     * 专项附加扣除
     */
    private BigDecimal specialDeduction;

    /**
     * 其他扣除
     */
    private BigDecimal otherDeduction;

    /**
     * 起征点
     */
    private BigDecimal threshold;

    /**
     * 应纳税所得额
     */
    private BigDecimal taxableIncome;

    /**
     * 适用税率
     */
    private BigDecimal taxRate;

    /**
     * 速算扣除数
     */
    private BigDecimal quickDeduction;

    /**
     * 应纳税额
     */
    private BigDecimal taxLiability;

    /**
     * 已预缴税额
     */
    private BigDecimal prepaidTax;

    /**
     * 应补（退）税额
     */
    private BigDecimal taxPayable;

    /**
     * 申报日期
     */
    private LocalDate declareDate;

    /**
     * 状态（DRAFT-草稿 SUBMITTED-已申报 PAID-已缴款）
     */
    private String status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 创建人ID
     */
    private Long createBy;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 更新人ID
     */
    private Long updateBy;

    /**
     * 租户ID
     */
    private Long tenantId;
}

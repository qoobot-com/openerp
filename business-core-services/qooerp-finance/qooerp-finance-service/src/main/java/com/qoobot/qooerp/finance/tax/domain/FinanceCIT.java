package com.qoobot.qooerp.finance.tax.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 企业所得税实体
 * 
 * @author QooERP Team
 * @since 20xx-xx-xx
 */
@Data
@TableName("finance_cit")
public class FinanceCIT {

    /**
     * 企业所得税ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 税务年度
     */
    private Integer taxYear;

    /**
     * 预缴季度（1-4）
     */
    private Integer quarter;

    /**
     * 税期类型（QUARTER-预缴 ANNUAL-汇算清缴）
     */
    private String taxPeriodType;

    /**
     * 税期开始日期
     */
    private LocalDate periodStart;

    /**
     * 税期结束日期
     */
    private LocalDate periodEnd;

    /**
     * 营业收入
     */
    private BigDecimal revenue;

    /**
     * 营业成本
     */
    private BigDecimal cost;

    /**
     * 利润总额
     */
    private BigDecimal totalProfit;

    /**
     * 纳税调整增加额
     */
    private BigDecimal taxAdjustmentIncrease;

    /**
     * 纳税调整减少额
     */
    private BigDecimal taxAdjustmentDecrease;

    /**
     * 应纳税所得额
     */
    private BigDecimal taxableIncome;

    /**
     * 适用税率（25%/20%/15%）
     */
    private BigDecimal taxRate;

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
     * 缴款期限
     */
    private LocalDate paymentDeadline;

    /**
     * 申报状态（DRAFT-草稿 SUBMITTED-已申报 PAID-已缴款 OVERDUE-逾期）
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

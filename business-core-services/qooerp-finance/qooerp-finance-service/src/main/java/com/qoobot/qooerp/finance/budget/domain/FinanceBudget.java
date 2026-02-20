package com.qoobot.qooerp.finance.budget.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.qoobot.qooerp.common.entity.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.time.YearMonth;

/**
 * 预算
 */
@Data
@TableName("finance_budget")
public class FinanceBudget extends BaseEntity {

    /**
     * 预算编号
     */
    private String budgetNo;

    /**
     * 预算类型（0-收入预算 1-支出预算）
     */
    private Integer budgetType;

    /**
     * 科目编码
     */
    private String accountCode;

    /**
     * 科目名称
     */
    private String accountName;

    /**
     * 成本中心ID
     */
    private Long costCenterId;

    /**
     * 预算年度
     */
    private Integer budgetYear;

    /**
     * 预算月份
     */
    private Integer budgetMonth;

    /**
     * 预算金额
     */
    private BigDecimal budgetAmount;

    /**
     * 已执行金额
     */
    private BigDecimal executedAmount;

    /**
     * 执行率
     */
    private BigDecimal executionRate;

    /**
     * 预算状态（0-草稿 1-审批中 2-已审批 3-已驳回）
     */
    private Integer status;

    /**
     * 制单人
     */
    private String creatorId;

    /**
     * 制单人姓名
     */
    private String creatorName;

    /**
     * 审批人
     */
    private String approverId;

    /**
     * 审批人姓名
     */
    private String approverName;

    /**
     * 审批时间
     */
    private java.time.LocalDateTime approveTime;

    /**
     * 驳回原因
     */
    private String rejectReason;

    /**
     * 备注
     */
    private String remark;
}

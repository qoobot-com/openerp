package com.qoobot.qooerp.scm.customer.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.qoobot.qooerp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 客户分级实体
 *
 * @author QooERP
 * @since 2026-02-18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("scm_customer_grade")
public class ScmCustomerGrade extends BaseEntity {

    /**
     * 客户ID
     */
    private Long customerId;

    /**
     * 客户编码
     */
    private String customerCode;

    /**
     * 分级日期
     */
    private java.time.LocalDate gradeDate;

    /**
     * 年采购金额
     */
    private BigDecimal annualPurchaseAmount;

    /**
     * 采购频次
     */
    private Integer purchaseCount;

    /**
     * 平均客单价
     */
    private BigDecimal averageOrderAmount;

    /**
     * 退货率
     */
    private BigDecimal returnRate;

    /**
     * 客户等级
     */
    private String customerLevel;

    /**
     * 等级变更原因
     */
    private String changeReason;
}

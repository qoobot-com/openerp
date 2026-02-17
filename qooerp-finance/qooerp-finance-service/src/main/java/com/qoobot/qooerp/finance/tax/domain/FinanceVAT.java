package com.qoobot.qooerp.finance.tax.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.qoobot.qooerp.common.entity.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 增值税
 */
@Data
@TableName("finance_vat")
public class FinanceVAT extends BaseEntity {

    /**
     * 税单编号
     */
    private String taxNo;

    /**
     * 税务期间
     */
    private String taxPeriod;

    /**
     * 销项税额（不含税）
     */
    private BigDecimal outputTaxExcl;

    /**
     * 销项税额（含税）
     */
    private BigDecimal outputTaxIncl;

    /**
     * 销项增值税额
     */
    private BigDecimal outputVAT;

    /**
     * 进项税额（不含税）
     */
    private BigDecimal inputTaxExcl;

    /**
     * 进项税额（含税）
     */
    private BigDecimal inputTaxIncl;

    /**
     * 进项增值税额
     */
    private BigDecimal inputVAT;

    /**
     * 进项税额已认证
     */
    private BigDecimal inputVATCertified;

    /**
     * 进项税额未认证
     */
    private BigDecimal inputVATUncertified;

    /**
     * 应交增值税额
     */
    private BigDecimal payableVAT;

    /**
     * 已交增值税额
     */
    private BigDecimal paidVAT;

    /**
     * 未交增值税额
     */
    private BigDecimal unpaidVAT;

    /**
     * 申报状态（0-未申报 1-已申报 2-已缴税）
     */
    private Integer declareStatus;

    /**
     * 申报日期
     */
    private LocalDate declareDate;

    /**
     * 备注
     */
    private String remark;
}

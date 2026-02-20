package com.qoobot.qooerp.scm.supplier.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.qoobot.qooerp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 供应商评估实体
 *
 * @author QooERP
 * @since 2026-02-18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("scm_supplier_evaluation")
public class ScmSupplierEvaluation extends BaseEntity {

    /**
     * 供应商ID
     */
    private Long supplierId;

    /**
     * 供应商编码
     */
    private String supplierCode;

    /**
     * 评估日期
     */
    private java.time.LocalDate evaluationDate;

    /**
     * 质量评分
     */
    private Integer qualityScore;

    /**
     * 交付评分
     */
    private Integer deliveryScore;

    /**
     * 服务评分
     */
    private Integer serviceScore;

    /**
     * 价格评分
     */
    private Integer priceScore;

    /**
     * 综合评分
     */
    private Integer totalScore;

    /**
     * 信用等级
     */
    private String creditLevel;

    /**
     * 评估人
     */
    private String evaluator;

    /**
     * 评估意见
     */
    private String evaluationRemark;
}

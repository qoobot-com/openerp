package com.qoobot.qooerp.scm.supplier.evaluation.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 供应商评估DTO
 *
 * @author QooERP
 * @since 2026-02-17
 */
@Data
public class SupplierEvaluationDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 供应商ID
     */
    private Long supplierId;

    /**
     * 供应商名称（冗余字段，用于展示）
     */
    private String supplierName;

    /**
     * 评估类型（季度/年度/专项）
     */
    private String evaluationType;

    /**
     * 质量得分（0-100）
     */
    private BigDecimal qualityScore;

    /**
     * 交期得分（0-100）
     */
    private BigDecimal deliveryScore;

    /**
     * 价格得分（0-100）
     */
    private BigDecimal priceScore;

    /**
     * 服务得分（0-100）
     */
    private BigDecimal serviceScore;

    /**
     * 综合得分
     */
    private BigDecimal totalScore;

    /**
     * 评估等级（优秀/良好/合格/不合格）
     */
    private String evaluationLevel;

    /**
     * 评估日期
     */
    private LocalDate evaluationDate;

    /**
     * 评估人
     */
    private String evaluator;

    /**
     * 评估内容
     */
    private String evaluationContent;

    /**
     * 改进计划
     */
    private String improvementPlan;

    /**
     * 租户ID
     */
    private Long tenantId;
}

package com.qoobot.qooerp.scm.supplier.evaluation.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.qoobot.qooerp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 供应商评估实体
 *
 * @author QooERP
 * @since 2026-02-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("scm_supplier_evaluation")
public class SupplierEvaluation extends BaseEntity {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 供应商ID
     */
    private Long supplierId;

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

    /**
     * 创建人
     */
    @TableField(fill = FieldFill.INSERT)
    private String createBy;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新人
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updateBy;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}

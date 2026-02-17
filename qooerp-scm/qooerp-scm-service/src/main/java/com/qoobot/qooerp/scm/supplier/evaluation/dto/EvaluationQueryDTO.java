package com.qoobot.qooerp.scm.supplier.evaluation.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * 供应商评估查询DTO
 *
 * @author QooERP
 * @since 2026-02-17
 */
@Data
public class EvaluationQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 页码
     */
    private Integer pageNo = 1;

    /**
     * 每页大小
     */
    private Integer pageSize = 10;

    /**
     * 供应商ID
     */
    private Long supplierId;

    /**
     * 评估类型（季度/年度/专项）
     */
    private String evaluationType;

    /**
     * 评估等级（优秀/良好/合格/不合格）
     */
    private String evaluationLevel;

    /**
     * 评估人
     */
    private String evaluator;

    /**
     * 评估日期开始
     */
    private LocalDate evaluationDateStart;

    /**
     * 评估日期结束
     */
    private LocalDate evaluationDateEnd;

    /**
     * 综合得分最小值
     */
    private java.math.BigDecimal totalScoreMin;

    /**
     * 综合得分最大值
     */
    private java.math.BigDecimal totalScoreMax;
}

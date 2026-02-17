package com.qoobot.qooerp.scm.supplier.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 供应商筛选DTO
 *
 * @author QooERP
 * @since 2026-02-17
 */
@Data
public class SupplierFilterDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 供应商名称（模糊查询）
     */
    private String supplierName;

    /**
     * 供应商类型列表（制造商/分销商/零售商）
     */
    private List<String> supplierTypes;

    /**
     * 信用等级列表
     */
    private List<String> creditRatings;

    /**
     * 省份列表
     */
    private List<String> provinces;

    /**
     * 城市列表
     */
    private List<String> cities;

    /**
     * 综合评估得分最小值
     */
    private BigDecimal minEvaluationScore;

    /**
     * 综合评估得分最大值
     */
    private BigDecimal maxEvaluationScore;

    /**
     * 评估等级列表（优秀/良好/合格/不合格）
     */
    private List<String> evaluationLevels;

    /**
     * 排序字段（创建时间/评估得分/信用等级等）
     */
    private String sortField;

    /**
     * 排序方向（asc/desc）
     */
    private String sortOrder;

    /**
     * 页码
     */
    private Integer pageNo = 1;

    /**
     * 每页大小
     */
    private Integer pageSize = 10;
}

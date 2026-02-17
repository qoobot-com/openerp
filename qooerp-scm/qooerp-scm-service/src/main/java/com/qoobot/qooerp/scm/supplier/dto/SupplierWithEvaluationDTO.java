package com.qoobot.qooerp.scm.supplier.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 供应商及其评估信息DTO
 *
 * @author QooERP
 * @since 2026-02-17
 */
@Data
public class SupplierWithEvaluationDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 供应商ID
     */
    private Long id;

    /**
     * 供应商编码
     */
    private String supplierCode;

    /**
     * 供应商名称
     */
    private String supplierName;

    /**
     * 供应商类型
     */
    private String supplierType;

    /**
     * 联系人
     */
    private String contactPerson;

    /**
     * 联系电话
     */
    private String contactPhone;

    /**
     * 联系邮箱
     */
    private String contactEmail;

    /**
     * 地址
     */
    private String address;

    /**
     * 城市
     */
    private String city;

    /**
     * 省份
     */
    private String province;

    /**
     * 国家
     */
    private String country;

    /**
     * 税号
     */
    private String taxNumber;

    /**
     * 信用等级
     */
    private String creditRating;

    /**
     * 付款期限（天）
     */
    private Integer paymentTerms;

    /**
     * 状态
     */
    private String status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 最新评估ID
     */
    private Long latestEvaluationId;

    /**
     * 最新评估得分
     */
    private BigDecimal latestEvaluationScore;

    /**
     * 最新评估等级
     */
    private String latestEvaluationLevel;

    /**
     * 最新评估日期
     */
    private java.time.LocalDate latestEvaluationDate;

    /**
     * 评估人
     */
    private String latestEvaluator;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}

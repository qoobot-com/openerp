package com.qoobot.qooerp.scm.supplier.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.qoobot.qooerp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 供应商实体
 *
 * @author QooERP
 * @since 2026-02-18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("scm_supplier")
public class ScmSupplier extends BaseEntity {

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
     * 所属行业
     */
    private String industry;

    /**
     * 企业规模
     */
    private String scale;

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
     * 联系地址
     */
    private String address;

    /**
     * 税号
     */
    private String taxNumber;

    /**
     * 开户银行
     */
    private String bankName;

    /**
     * 银行账号
     */
    private String bankAccount;

    /**
     * 信用等级
     */
    private String creditLevel;

    /**
     * 信用额度
     */
    private BigDecimal creditLimit;

    /**
     * 结算方式
     */
    private String paymentMethod;

    /**
     * 付款期限（天）
     */
    private Integer paymentDays;

    /**
     * 评估总分
     */
    private Integer evaluationScore;

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
     * 最后评估时间
     */
    private java.time.LocalDateTime lastEvaluationTime;

    /**
     * 状态
     */
    private String status;

    /**
     * 备注
     */
    private String remark;
}

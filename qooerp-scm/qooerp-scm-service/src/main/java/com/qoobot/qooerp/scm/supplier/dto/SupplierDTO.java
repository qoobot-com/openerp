package com.qoobot.qooerp.scm.supplier.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 供应商DTO
 *
 * @author QooERP
 * @since 2026-02-17
 */
@Data
public class SupplierDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
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
     * 邮政编码
     */
    private String postalCode;

    /**
     * 税号
     */
    private String taxNumber;

    /**
     * 银行名称
     */
    private String bankName;

    /**
     * 银行账号
     */
    private String bankAccount;

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
     * 租户ID
     */
    private Long tenantId;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新人
     */
    private String updateBy;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}

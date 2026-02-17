package com.qoobot.qooerp.scm.customer.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 客户DTO
 *
 * @author QooERP
 * @since 2026-02-17
 */
@Data
public class CustomerDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 客户编码
     */
    private String customerCode;

    /**
     * 客户名称
     */
    private String customerName;

    /**
     * 客户类型
     */
    private String customerType;

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
     * 信用等级
     */
    private String creditRating;

    /**
     * 信用额度
     */
    private BigDecimal creditLimit;

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

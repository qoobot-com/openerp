package com.qoobot.qooerp.scm.customer.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.qoobot.qooerp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 客户实体
 *
 * @author QooERP
 * @since 2026-02-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("scm_customer")
public class Customer extends BaseEntity {

    /**
     * 客户编码
     */
    private String customerCode;

    /**
     * 客户名称
     */
    private String customerName;

    /**
     * 客户类型（个人/企业）
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
     * 状态（启用/禁用）
     */
    private String status;

    /**
     * 备注
     */
    private String remark;
}

package com.qoobot.qooerp.scm.customer.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.qoobot.qooerp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 客户实体
 *
 * @author QooERP
 * @since 2026-02-18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("scm_customer")
public class ScmCustomer extends BaseEntity {

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
     * 所属行业
     */
    private String industry;

    /**
     * 企业规模
     */
    private String scale;

    /**
     * 客户等级
     */
    private String customerLevel;

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
     * 状态
     */
    private String status;

    /**
     * 备注
     */
    private String remark;
}

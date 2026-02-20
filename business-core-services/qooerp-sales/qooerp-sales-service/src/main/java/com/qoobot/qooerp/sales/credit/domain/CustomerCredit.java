package com.qoobot.qooerp.sales.credit.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qoobot.qooerp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;

/**
 * 客户信用实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("customer_credit")
public class CustomerCredit extends BaseEntity {

    /** 客户ID */
    @TableField("customer_id")
    private Long customerId;

    /** 客户名称 */
    @TableField("customer_name")
    private String customerName;

    /** 信用额度 */
    @TableField("credit_limit")
    private BigDecimal creditLimit;

    /** 已用额度 */
    @TableField("credit_used")
    private BigDecimal creditUsed;

    /** 信用等级 */
    @TableField("credit_level")
    private String creditLevel;

    /** 信用状态 */
    @TableField("credit_status")
    private String creditStatus;

    /** 备注 */
    @TableField("remark")
    private String remark;
}

package com.qoobot.qooerp.scm.customer.level.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.qoobot.qooerp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 客户等级实体
 *
 * @author QooERP
 * @since 2026-02-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("scm_customer_level")
public class CustomerLevel extends BaseEntity {

    /**
     * 等级编码
     */
    private String levelCode;

    /**
     * 等级名称
     */
    private String levelName;

    /**
     * 最低交易金额
     */
    private BigDecimal minAmount;

    /**
     * 最高交易金额
     */
    private BigDecimal maxAmount;

    /**
     * 最低订单数
     */
    private Integer minOrders;

    /**
     * 折扣率
     */
    private BigDecimal discountRate;

    /**
     * 优先级
     */
    private Integer priority;

    /**
     * 状态（启用/禁用）
     */
    private String status;

    /**
     * 备注
     */
    private String remark;
}

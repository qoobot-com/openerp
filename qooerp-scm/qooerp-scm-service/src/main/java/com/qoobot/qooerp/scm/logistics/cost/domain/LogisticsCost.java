package com.qoobot.qooerp.scm.logistics.cost.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.qoobot.qooerp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 物流费用实体
 *
 * @author QooERP
 * @since 2026-02-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("scm_logistics_cost")
public class LogisticsCost extends BaseEntity {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 物流跟踪ID
     */
    private Long trackingId;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 费用类型（运费/包装费/保险费/其他）
     */
    private String costType;

    /**
     * 金额
     */
    private BigDecimal amount;

    /**
     * 币种
     */
    private String currency;

    /**
     * 付款状态（未付/已付/部分）
     */
    private String paidStatus;

    /**
     * 已付金额
     */
    private BigDecimal paidAmount;

    /**
     * 付款日期
     */
    private LocalDate paidDate;

    /**
     * 发票号码
     */
    private String invoiceNumber;

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
    @TableField(fill = FieldFill.INSERT)
    private String createBy;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新人
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updateBy;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}

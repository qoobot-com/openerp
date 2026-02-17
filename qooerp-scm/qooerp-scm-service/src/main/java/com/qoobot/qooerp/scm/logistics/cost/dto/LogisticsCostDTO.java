package com.qoobot.qooerp.scm.logistics.cost.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 物流费用DTO
 *
 * @author QooERP
 * @since 2026-02-17
 */
@Data
public class LogisticsCostDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
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
}

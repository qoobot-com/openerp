package com.qoobot.qooerp.scm.logistics.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.qoobot.qooerp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 物流实体
 *
 * @author QooERP
 * @since 2026-02-18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("scm_logistics")
public class ScmLogistics extends BaseEntity {

    /**
     * 物流单号
     */
    private String logisticsCode;

    /**
     * 业务类型
     */
    private String businessType;

    /**
     * 关联单据ID
     */
    private Long relatedOrderId;

    /**
     * 关联单据号
     */
    private String relatedOrderCode;

    /**
     * 物流公司
     */
    private String logisticsCompany;

    /**
     * 快递单号
     */
    private String trackingNumber;

    /**
     * 发货人
     */
    private String sender;

    /**
     * 发货人电话
     */
    private String senderPhone;

    /**
     * 发货地址
     */
    private String senderAddress;

    /**
     * 收货人
     */
    private String receiver;

    /**
     * 收货人电话
     */
    private String receiverPhone;

    /**
     * 收货地址
     */
    private String receiverAddress;

    /**
     * 发货日期
     */
    private LocalDate shipmentDate;

    /**
     * 预计到达日期
     */
    private LocalDate estimatedArrivalDate;

    /**
     * 实际到达日期
     */
    private LocalDate actualArrivalDate;

    /**
     * 物流费用
     */
    private BigDecimal logisticsCost;

    /**
     * 物流状态
     */
    private String logisticsStatus;

    /**
     * 备注
     */
    private String remark;
}

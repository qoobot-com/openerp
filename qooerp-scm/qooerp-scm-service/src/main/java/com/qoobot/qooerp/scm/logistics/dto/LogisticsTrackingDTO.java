package com.qoobot.qooerp.scm.logistics.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 物流跟踪DTO
 *
 * @author QooERP
 * @since 2026-02-17
 */
@Data
public class LogisticsTrackingDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 运单号
     */
    private String trackingNumber;

    /**
     * 承运商
     */
    private String carrier;

    /**
     * 承运商编码
     */
    private String carrierCode;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 发货地址
     */
    private String fromAddress;

    /**
     * 收货地址
     */
    private String toAddress;

    /**
     * 发货人
     */
    private String sender;

    /**
     * 收货人
     */
    private String receiver;

    /**
     * 收货人电话
     */
    private String receiverPhone;

    /**
     * 发货时间
     */
    private LocalDateTime shipmentDate;

    /**
     * 预计送达时间
     */
    private LocalDateTime estimatedDelivery;

    /**
     * 实际送达时间
     */
    private LocalDateTime actualDelivery;

    /**
     * 状态（待发货/运输中/已签收/异常）
     */
    private String status;

    /**
     * 当前位置
     */
    private String currentLocation;

    /**
     * 重量（kg）
     */
    private BigDecimal weight;

    /**
     * 体积（m³）
     */
    private BigDecimal volume;

    /**
     * 包裹数量
     */
    private Integer packages;

    /**
     * 备注
     */
    private String remark;

    /**
     * 租户ID
     */
    private Long tenantId;
}

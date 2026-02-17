package com.qoobot.qooerp.scm.logistics.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 物流跟踪查询DTO
 *
 * @author QooERP
 * @since 2026-02-17
 */
@Data
public class TrackingQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 页码
     */
    private Integer pageNo = 1;

    /**
     * 每页大小
     */
    private Integer pageSize = 10;

    /**
     * 运单号（模糊查询）
     */
    private String trackingNumber;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 承运商编码
     */
    private String carrierCode;

    /**
     * 收货人（模糊查询）
     */
    private String receiver;

    /**
     * 收货人电话（模糊查询）
     */
    private String receiverPhone;

    /**
     * 状态（待发货/运输中/已签收/异常）
     */
    private String status;

    /**
     * 发货时间开始
     */
    private LocalDateTime shipmentDateStart;

    /**
     * 发货时间结束
     */
    private LocalDateTime shipmentDateEnd;

    /**
     * 预计送达时间开始
     */
    private LocalDateTime estimatedDeliveryStart;

    /**
     * 预计送达时间结束
     */
    private LocalDateTime estimatedDeliveryEnd;
}

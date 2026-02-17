package com.qoobot.qooerp.scm.logistics.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.qoobot.qooerp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 物流跟踪实体
 *
 * @author QooERP
 * @since 2026-02-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("scm_logistics_tracking")
public class LogisticsTracking extends BaseEntity {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
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

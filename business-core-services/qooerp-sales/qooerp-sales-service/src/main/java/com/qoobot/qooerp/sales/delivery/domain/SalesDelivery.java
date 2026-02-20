package com.qoobot.qooerp.sales.delivery.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qoobot.qooerp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 发货单实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sales_delivery")
public class SalesDelivery extends BaseEntity {

    /** 发货单编号 */
    @TableField("delivery_no")
    private String deliveryNo;

    /** 订单ID */
    @TableField("order_id")
    private Long orderId;

    /** 订单编号 */
    @TableField("order_no")
    private String orderNo;

    /** 客户ID */
    @TableField("customer_id")
    private Long customerId;

    /** 客户名称 */
    @TableField("customer_name")
    private String customerName;

    /** 发货日期 */
    @TableField("delivery_date")
    private LocalDate deliveryDate;

    /** 发货金额 */
    @TableField("delivery_amount")
    private BigDecimal deliveryAmount;

    /** 收货地址 */
    @TableField("address")
    private String address;

    /** 联系人 */
    @TableField("contact")
    private String contact;

    /** 联系电话 */
    @TableField("phone")
    private String phone;

    /** 物流公司 */
    @TableField("logistics_company")
    private String logisticsCompany;

    /** 物流单号 */
    @TableField("tracking_no")
    private String trackingNo;

    /** 发货状态 */
    @TableField("status")
    private String status;

    /** 签收人 */
    @TableField("receiver")
    private String receiver;

    /** 签收时间 */
    @TableField("receive_time")
    private LocalDateTime receiveTime;

    /** 备注 */
    @TableField("remark")
    private String remark;
}

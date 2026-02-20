package com.qoobot.qooerp.sales.response.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qoobot.qooerp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 退货单实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sales_return")
public class SalesReturn extends BaseEntity {

    /** 退货单编号 */
    @TableField("return_no")
    private String returnNo;

    /** 订单ID */
    @TableField("order_id")
    private Long orderId;

    /** 订单编号 */
    @TableField("order_no")
    private String orderNo;

    /** 发货单ID */
    @TableField("delivery_id")
    private Long deliveryId;

    /** 客户ID */
    @TableField("customer_id")
    private Long customerId;

    /** 客户名称 */
    @TableField("customer_name")
    private String customerName;

    /** 退货日期 */
    @TableField("return_date")
    private LocalDate returnDate;

    /** 退货金额 */
    @TableField("return_amount")
    private BigDecimal returnAmount;

    /** 总金额 */
    @TableField("total_amount")
    private BigDecimal totalAmount;

    /** 退款金额 */
    @TableField("refund_amount")
    private BigDecimal refundAmount;

    /** 退货原因 */
    @TableField("reason")
    private String reason;

    /** 退货状态 */
    @TableField("status")
    private String status;

    /** 审核人ID */
    @TableField("approver_id")
    private Long approverId;

    /** 审核意见 */
    @TableField("approve_remark")
    private String approveRemark;

    /** 审核时间 */
    @TableField("approve_time")
    private LocalDateTime approveTime;

    /** 备注 */
    @TableField("remark")
    private String remark;
}

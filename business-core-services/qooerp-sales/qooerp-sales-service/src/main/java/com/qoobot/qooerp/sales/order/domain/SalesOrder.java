package com.qoobot.qooerp.sales.order.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qoobot.qooerp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 销售订单实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sales_order")
public class SalesOrder extends BaseEntity {

    /** 订单编号 */
    @TableField("order_code")
    private String orderCode;

    /** 客户ID */
    @TableField("customer_id")
    private Long customerId;

    /** 客户名称 */
    @TableField("customer_name")
    private String customerName;

    /** 订单日期 */
    @TableField("order_date")
    private LocalDate orderDate;

    /** 交货日期 */
    @TableField("delivery_date")
    private LocalDate deliveryDate;

    /** 订单金额 */
    @TableField("order_amount")
    private BigDecimal orderAmount;

    /** 折扣金额 */
    @TableField("discount_amount")
    private BigDecimal discountAmount;

    /** 实付金额 */
    @TableField("payable_amount")
    private BigDecimal payableAmount;

    /** 订单状态 */
    @TableField("order_status")
    private String orderStatus;

    /** 订单类型 */
    @TableField("order_type")
    private String orderType;

    /** 销售员ID */
    @TableField("salesperson_id")
    private Long salespersonId;

    /** 销售员姓名 */
    @TableField("salesperson_name")
    private String salespersonName;

    /** 仓库ID */
    @TableField("warehouse_id")
    private Long warehouseId;

    /** 仓库名称 */
    @TableField("warehouse_name")
    private String warehouseName;

    /** 备注 */
    @TableField("remark")
    private String remark;
}

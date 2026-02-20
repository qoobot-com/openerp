package com.qoobot.qooerp.sales.order.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qoobot.qooerp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;

/**
 * 销售订单明细实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sales_order_detail")
public class SalesOrderDetail extends BaseEntity {

    /** 订单ID */
    @TableField("order_id")
    private Long orderId;

    /** 物料ID */
    @TableField("material_id")
    private Long materialId;

    /** 物料编码 */
    @TableField("material_code")
    private String materialCode;

    /** 物料名称 */
    @TableField("material_name")
    private String materialName;

    /** 规格型号 */
    @TableField("specification")
    private String specification;

    /** 计量单位 */
    @TableField("unit")
    private String unit;

    /** 数量 */
    @TableField("quantity")
    private BigDecimal quantity;

    /** 单价 */
    @TableField("unit_price")
    private BigDecimal unitPrice;

    /** 金额 */
    @TableField("amount")
    private BigDecimal amount;

    /** 折扣率 */
    @TableField("discount_rate")
    private BigDecimal discountRate;

    /** 折扣金额 */
    @TableField("discount_amount")
    private BigDecimal discountAmount;

    /** 实付金额 */
    @TableField("payable_amount")
    private BigDecimal payableAmount;

    /** 已发货数量 */
    @TableField("shipped_quantity")
    private BigDecimal shippedQuantity;

    /** 备注 */
    @TableField("remark")
    private String remark;
}

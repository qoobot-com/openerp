package com.qoobot.qooerp.sales.contract.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qoobot.qooerp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;

/**
 * 销售合同明细实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sales_contract_detail")
public class SalesContractDetail extends BaseEntity {

    /** 合同ID */
    @TableField("contract_id")
    private Long contractId;

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

    /** 备注 */
    @TableField("remark")
    private String remark;
}

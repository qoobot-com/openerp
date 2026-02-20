package com.qoobot.qooerp.sales.contract.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qoobot.qooerp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 销售合同实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sales_contract")
public class SalesContract extends BaseEntity {

    /** 合同编号 */
    @TableField("contract_no")
    private String contractNo;

    /** 合同类型 */
    @TableField("contract_type")
    private String contractType;

    /** 客户ID */
    @TableField("customer_id")
    private Long customerId;

    /** 客户名称 */
    @TableField("customer_name")
    private String customerName;

    /** 签订日期 */
    @TableField("sign_date")
    private LocalDate signDate;

    /** 开始日期 */
    @TableField("start_date")
    private LocalDate startDate;

    /** 结束日期 */
    @TableField("end_date")
    private LocalDate endDate;

    /** 合同金额 */
    @TableField("contract_amount")
    private BigDecimal contractAmount;

    /** 折扣金额 */
    @TableField("discount_amount")
    private BigDecimal discountAmount;

    /** 实付金额 */
    @TableField("payable_amount")
    private BigDecimal payableAmount;

    /** 已支付金额 */
    @TableField("paid_amount")
    private BigDecimal paidAmount;

    /** 合同状态 */
    @TableField("status")
    private String status;

    /** 销售员ID */
    @TableField("salesperson_id")
    private Long salespersonId;

    /** 销售员姓名 */
    @TableField("salesperson_name")
    private String salespersonName;

    /** 审核人ID */
    @TableField("approver_id")
    private Long approverId;

    /** 审核意见 */
    @TableField("approve_remark")
    private String approveRemark;

    /** 审核时间 */
    @TableField("approve_time")
    private LocalDateTime approveTime;

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

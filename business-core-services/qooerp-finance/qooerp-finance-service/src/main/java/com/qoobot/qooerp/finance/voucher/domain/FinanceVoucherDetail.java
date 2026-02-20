package com.qoobot.qooerp.finance.voucher.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.qoobot.qooerp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 凭证明细实体
 *
 * @author QooERP Team
 * @since 20xx-xx-xx
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("finance_voucher_detail")
public class FinanceVoucherDetail extends BaseEntity {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 租户ID
     */
    @TableField("tenant_id")
    private Long tenantId;

    /**
     * 凭证ID
     */
    @TableField("voucher_id")
    private Long voucherId;

    /**
     * 科目编码
     */
    @TableField("account_code")
    private String accountCode;

    /**
     * 科目名称
     */
    @TableField("account_name")
    private String accountName;

    /**
     * 借贷方向：DEBIT-借，CREDIT-贷
     */
    @TableField("direction")
    private String direction;

    /**
     * 金额
     */
    @TableField("amount")
    private BigDecimal amount;

    /**
     * 摘要
     */
    @TableField("summary")
    private String summary;

    /**
     * 辅助核算类型：CUSTOMER-客户，SUPPLIER-供应商，DEPARTMENT-部门，PROJECT-项目，EMPLOYEE-员工
     */
    @TableField("auxiliary_type")
    private String auxiliaryType;

    /**
     * 辅助核算ID
     */
    @TableField("auxiliary_id")
    private Long auxiliaryId;

    /**
     * 排序号
     */
    @TableField("sort_order")
    private Integer sortOrder;

    /**
     * 删除标记：0-未删除，1-已删除
     */
    @TableLogic
    @TableField("deleted")
    private Integer deleted;
}

package com.qoobot.qooerp.finance.accounting.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.qoobot.qooerp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.List;

/**
 * 会计科目实体
 *
 * @author QooERP Team
 * @since 2026-02-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("finance_account")
public class FinanceAccount extends BaseEntity {

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
     * 科目类型：ASSET-资产，LIABILITY-负债，EQUITY-所有者权益，REVENUE-收入，EXPENSE-费用
     */
    @TableField("account_type")
    private String accountType;

    /**
     * 科目级次
     */
    @TableField("level")
    private Integer level;

    /**
     * 父科目ID
     */
    @TableField("parent_id")
    private Long parentId;

    /**
     * 是否末级：0-否，1-是
     */
    @TableField("is_leaf")
    private Integer isLeaf;

    /**
     * 借贷方向：DEBIT-借，CREDIT-贷
     */
    @TableField("direction")
    private String direction;

    /**
     * 余额方向：DEBIT-借，CREDIT-贷
     */
    @TableField("balance_direction")
    private String balanceDirection;

    /**
     * 是否启用：0-否，1-是
     */
    @TableField("is_enabled")
    private Integer isEnabled;

    /**
     * 是否现金科目：0-否，1-是
     */
    @TableField("is_cash_account")
    private Integer isCashAccount;

    /**
     * 是否银行科目：0-否，1-是
     */
    @TableField("is_bank_account")
    private Integer isBankAccount;

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

    /**
     * 子科目列表（非数据库字段，用于树形结构）
     */
    @TableField(exist = false)
    private List<FinanceAccount> children;
}

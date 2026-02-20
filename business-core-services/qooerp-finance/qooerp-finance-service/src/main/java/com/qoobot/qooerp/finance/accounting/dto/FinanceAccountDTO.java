package com.qoobot.qooerp.finance.accounting.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 会计科目DTO
 *
 * @author QooERP Team
 * @since 2026-02-17
 */
@Data
public class FinanceAccountDTO {

    /**
     * 科目ID（更新时必填）
     */
    private Long id;

    /**
     * 租户ID
     */
    @NotNull(message = "租户ID不能为空")
    private Long tenantId;

    /**
     * 科目编码
     */
    @NotBlank(message = "科目编码不能为空")
    private String accountCode;

    /**
     * 科目名称
     */
    @NotBlank(message = "科目名称不能为空")
    private String accountName;

    /**
     * 科目类型：ASSET-资产，LIABILITY-负债，EQUITY-所有者权益，REVENUE-收入，EXPENSE-费用
     */
    @NotBlank(message = "科目类型不能为空")
    private String accountType;

    /**
     * 科目级次
     */
    @NotNull(message = "科目级次不能为空")
    private Integer level;

    /**
     * 父科目ID
     */
    private Long parentId;

    /**
     * 是否末级：0-否，1-是
     */
    private Integer isLeaf;

    /**
     * 借贷方向：DEBIT-借，CREDIT-贷
     */
    @NotBlank(message = "借贷方向不能为空")
    private String direction;

    /**
     * 余额方向：DEBIT-借，CREDIT-贷
     */
    @NotBlank(message = "余额方向不能为空")
    private String balanceDirection;

    /**
     * 是否启用：0-否，1-是
     */
    private Integer isEnabled;

    /**
     * 是否现金科目：0-否，1-是
     */
    private Integer isCashAccount;

    /**
     * 是否银行科目：0-否，1-是
     */
    private Integer isBankAccount;

    /**
     * 排序号
     */
    private Integer sortOrder;

    /**
     * 备注
     */
    private String remark;
}

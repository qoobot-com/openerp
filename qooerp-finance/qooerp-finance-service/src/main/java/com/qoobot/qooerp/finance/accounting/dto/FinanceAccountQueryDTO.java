package com.qoobot.qooerp.finance.accounting.dto;

import com.qoobot.qooerp.common.dto.PageQueryDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 会计科目查询DTO
 *
 * @author QooERP Team
 * @since 2026-02-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FinanceAccountQueryDTO extends PageQueryDTO {

    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * 科目编码（模糊查询）
     */
    private String accountCode;

    /**
     * 科目名称（模糊查询）
     */
    private String accountName;

    /**
     * 科目类型
     */
    private String accountType;

    /**
     * 是否启用
     */
    private Integer isEnabled;
}

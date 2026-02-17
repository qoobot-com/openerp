package com.qoobot.qooerp.finance.voucher.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 凭证明细DTO
 *
 * @author QooERP Team
 * @since 2026-02-17
 */
@Data
@Schema(description = "凭证明细DTO")
public class FinanceVoucherDetailDTO {

    @Schema(description = "明细ID")
    private Long id;

    @Schema(description = "凭证ID")
    private Long voucherId;

    @Schema(description = "明细行号")
    private Integer lineNo;

    @Schema(description = "科目编码")
    private String accountCode;

    @Schema(description = "科目名称")
    private String accountName;

    @Schema(description = "借方金额")
    private BigDecimal debitAmount;

    @Schema(description = "贷方金额")
    private BigDecimal creditAmount;

    @Schema(description = "辅助核算类型（CUSTOMER-客户 SUPPLIER-供应商 DEPARTMENT-部门 PROJECT-项目 EMPLOYEE-员工）")
    private String auxiliaryType;

    @Schema(description = "辅助核算ID")
    private Long auxiliaryId;

    @Schema(description = "辅助核算名称")
    private String auxiliaryName;

    @Schema(description = "摘要")
    private String summary;
}

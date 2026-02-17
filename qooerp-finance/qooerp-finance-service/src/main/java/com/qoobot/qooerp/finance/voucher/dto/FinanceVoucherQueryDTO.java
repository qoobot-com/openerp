package com.qoobot.qooerp.finance.voucher.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

/**
 * 财务凭证查询DTO
 *
 * @author QooERP Team
 * @since 2026-02-17
 */
@Data
@Schema(description = "财务凭证查询DTO")
public class FinanceVoucherQueryDTO {

    @Schema(description = "凭证编号（模糊查询）")
    private String voucherCode;

    @Schema(description = "开始日期")
    private LocalDate startDate;

    @Schema(description = "结束日期")
    private LocalDate endDate;

    @Schema(description = "凭证类型")
    private String voucherType;

    @Schema(description = "审核状态")
    private String auditStatus;

    @Schema(description = "记账状态")
    private String postingStatus;

    @Schema(description = "摘要（模糊查询）")
    private String summary;

    @Schema(description = "制单人ID")
    private Long makerId;

    @Schema(description = "科目编码")
    private String accountCode;

    @Schema(description = "页码")
    private Integer pageNo = 1;

    @Schema(description = "每页大小")
    private Integer pageSize = 10;
}

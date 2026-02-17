package com.qoobot.qooerp.finance.arap.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

/**
 * 应付账款查询DTO
 *
 * @author QooERP Team
 * @since 2026-02-17
 */
@Data
@Schema(description = "应付账款查询DTO")
public class PayableQueryDTO {

    @Schema(description = "应付单号（模糊查询）")
    private String payableCode;

    @Schema(description = "供应商名称（模糊查询）")
    private String supplierName;

    @Schema(description = "业务类型")
    private String businessType;

    @Schema(description = "开始日期")
    private LocalDate startDate;

    @Schema(description = "结束日期")
    private LocalDate endDate;

    @Schema(description = "单据状态")
    private String status;

    @Schema(description = "账龄范围（天）- 开始")
    private Integer agingDaysStart;

    @Schema(description = "账龄范围（天）- 结束")
    private Integer agingDaysEnd;

    @Schema(description = "页码")
    private Integer pageNo = 1;

    @Schema(description = "每页大小")
    private Integer pageSize = 10;
}

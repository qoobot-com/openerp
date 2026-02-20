package com.qoobot.qooerp.finance.report.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 利润表查询DTO
 *
 * @author QooERP Team
 * @since 20xx-xx-xx
 */
@Data
@Schema(description = "利润表查询DTO")
public class IncomeStatementQueryDTO {

    @Schema(description = "报表编号（模糊查询）")
    private String reportCode;

    @Schema(description = "报表年份")
    private Integer reportYear;

    @Schema(description = "报表期间")
    private String reportPeriod;

    @Schema(description = "报表类型")
    private String reportType;

    @Schema(description = "账套ID")
    private Long ledgerId;

    @Schema(description = "页码")
    private Integer pageNo = 1;

    @Schema(description = "每页大小")
    private Integer pageSize = 10;
}

package com.qoobot.qooerp.finance.period.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 会计期间查询DTO
 *
 * @author QooERP Team
 * @since 2026-02-17
 */
@Data
@Schema(description = "会计期间查询DTO")
public class PeriodQueryDTO {

    @Schema(description = "会计年度")
    private Integer fiscalYear;

    @Schema(description = "会计期间")
    private String fiscalPeriod;

    @Schema(description = "期间状态")
    private String status;

    @Schema(description = "开始日期")
    private String startDate;

    @Schema(description = "结束日期")
    private String endDate;

    @Schema(description = "页码")
    private Integer pageNo = 1;

    @Schema(description = "每页大小")
    private Integer pageSize = 10;
}

package com.qoobot.qooerp.finance.tax.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 个人所得税查询DTO
 *
 * @author QooERP Team
 * @since 2026-02-17
 */
@Data
@Schema(description = "个人所得税查询DTO")
public class PITQueryDTO {

    @Schema(description = "税单编号（模糊查询）")
    private String taxCode;

    @Schema(description = "税种")
    private String taxType;

    @Schema(description = "纳税年度")
    private Integer taxYear;

    @Schema(description = "纳税期间")
    private String taxPeriod;

    @Schema(description = "员工ID")
    private Long employeeId;

    @Schema(description = "员工姓名（模糊查询）")
    private String employeeName;

    @Schema(description = "开始日期")
    private LocalDate startDate;

    @Schema(description = "结束日期")
    private LocalDate endDate;

    @Schema(description = "应纳税所得额范围-开始")
    private BigDecimal taxableIncomeStart;

    @Schema(description = "应纳税所得额范围-结束")
    private BigDecimal taxableIncomeEnd;

    @Schema(description = "页码")
    private Integer pageNo = 1;

    @Schema(description = "每页大小")
    private Integer pageSize = 10;
}

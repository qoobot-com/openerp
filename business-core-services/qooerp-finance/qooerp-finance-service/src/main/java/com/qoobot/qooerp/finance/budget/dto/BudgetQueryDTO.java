package com.qoobot.qooerp.finance.budget.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 预算查询DTO
 *
 * @author QooERP Team
 * @since 20xx-xx-xx
 */
@Data
@Schema(description = "预算查询DTO")
public class BudgetQueryDTO {

    @Schema(description = "预算编号（模糊查询）")
    private String budgetCode;

    @Schema(description = "预算名称（模糊查询）")
    private String budgetName;

    @Schema(description = "预算年度")
    private Integer budgetYear;

    @Schema(description = "预算类型")
    private String budgetType;

    @Schema(description = "预算科目ID")
    private Long accountId;

    @Schema(description = "成本中心ID")
    private Long costCenterId;

    @Schema(description = "部门ID")
    private Long departmentId;

    @Schema(description = "预算状态")
    private String status;

    @Schema(description = "预算金额范围-开始")
    private BigDecimal amountStart;

    @Schema(description = "预算金额范围-结束")
    private BigDecimal amountEnd;

    @Schema(description = "创建开始日期")
    private LocalDate startDate;

    @Schema(description = "创建结束日期")
    private LocalDate endDate;

    @Schema(description = "页码")
    private Integer pageNo = 1;

    @Schema(description = "每页大小")
    private Integer pageSize = 10;
}

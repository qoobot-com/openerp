package com.qoobot.qooerp.finance.budget.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 预算DTO
 *
 * @author QooERP Team
 * @since 2026-02-17
 */
@Data
@Schema(description = "预算DTO")
public class FinanceBudgetDTO {

    @Schema(description = "预算ID")
    private Long id;

    @Schema(description = "预算编号")
    private String budgetNo;

    @Schema(description = "预算类型（0-收入预算 1-支出预算）")
    private Integer budgetType;

    @Schema(description = "科目编码")
    private String accountCode;

    @Schema(description = "科目名称")
    private String accountName;

    @Schema(description = "成本中心ID")
    private Long costCenterId;

    @Schema(description = "预算年度")
    private Integer budgetYear;

    @Schema(description = "预算月份")
    private Integer budgetMonth;

    @Schema(description = "预算金额")
    private BigDecimal budgetAmount;

    @Schema(description = "已执行金额")
    private BigDecimal executedAmount;

    @Schema(description = "执行率")
    private BigDecimal executionRate;

    @Schema(description = "预算状态（0-草稿 1-审批中 2-已审批 3-已驳回）")
    private Integer status;

    @Schema(description = "制单人")
    private String creatorId;

    @Schema(description = "制单人姓名")
    private String creatorName;

    @Schema(description = "审批人")
    private String approverId;

    @Schema(description = "审批人姓名")
    private String approverName;

    @Schema(description = "审批时间")
    private LocalDateTime approveTime;

    @Schema(description = "驳回原因")
    private String rejectReason;

    @Schema(description = "备注")
    private String remark;
}

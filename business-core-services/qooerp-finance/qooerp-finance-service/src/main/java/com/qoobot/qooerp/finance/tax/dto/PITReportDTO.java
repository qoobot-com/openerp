package com.qoobot.qooerp.finance.tax.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 个人所得税报表DTO
 *
 * @author QooERP Team
 * @since 20xx-xx-xx
 */
@Data
@Schema(description = "个人所得税报表DTO")
public class PITReportDTO {

    @Schema(description = "PIT记录ID")
    private Long id;

    @Schema(description = "员工ID")
    private Long employeeId;

    @Schema(description = "员工姓名")
    private String employeeName;

    @Schema(description = "会计年度")
    private Integer fiscalYear;

    @Schema(description = "月份")
    private Integer month;

    @Schema(description = "收入")
    private BigDecimal salary;

    @Schema(description = "扣除项")
    private BigDecimal deductions;

    @Schema(description = "应纳税所得额")
    private BigDecimal taxableIncome;

    @Schema(description = "适用税率")
    private BigDecimal applicableRate;

    @Schema(description = "速算扣除数")
    private BigDecimal quickDeduction;

    @Schema(description = "应纳税额")
    private BigDecimal taxAmount;

    @Schema(description = "已扣缴金额")
    private BigDecimal withheldAmount;

    @Schema(description = "计算日期")
    private LocalDate calculateDate;

    @Schema(description = "备注")
    private String remark;
}

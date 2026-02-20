package com.qoobot.qooerp.finance.period.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 会计期间DTO
 *
 * @author QooERP Team
 * @since 20xx-xx-xx
 */
@Data
@Schema(description = "会计期间DTO")
public class FinancePeriodDTO {

    @Schema(description = "期间ID")
    private Long id;

    @Schema(description = "期间编码")
    private String periodCode;

    @Schema(description = "会计年度")
    private Integer fiscalYear;

    @Schema(description = "会计期间（1-12）")
    private Integer periodNo;

    @Schema(description = "开始日期")
    private LocalDate startDate;

    @Schema(description = "结束日期")
    private LocalDate endDate;

    @Schema(description = "期间状态（0-未开始 1-进行中 2-已结账）")
    private Integer status;

    @Schema(description = "是否允许录入凭证（0-不允许 1-允许）")
    private Integer allowEntry;

    @Schema(description = "结账人ID")
    private String closerId;

    @Schema(description = "结账人姓名")
    private String closerName;

    @Schema(description = "结账时间")
    private LocalDateTime closeTime;

    @Schema(description = "反结账次数")
    private Integer reopenCount;

    @Schema(description = "备注")
    private String remark;
}

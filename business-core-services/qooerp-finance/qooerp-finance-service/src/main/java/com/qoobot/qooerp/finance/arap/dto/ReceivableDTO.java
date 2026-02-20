package com.qoobot.qooerp.finance.arap.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 应收账款DTO
 *
 * @author QooERP Team
 * @since 20xx-xx-xx
 */
@Data
@Schema(description = "应收账款DTO")
public class ReceivableDTO {

    @Schema(description = "应收单ID")
    private Long id;

    @Schema(description = "应收单号")
    private String receivableCode;

    @Schema(description = "客户ID")
    private Long customerId;

    @Schema(description = "客户名称")
    private String customerName;

    @Schema(description = "业务类型")
    private String businessType;

    @Schema(description = "关联单据ID")
    private Long relatedOrderId;

    @Schema(description = "关联单据号")
    private String relatedOrderCode;

    @Schema(description = "应收金额")
    private BigDecimal receivableAmount;

    @Schema(description = "已收金额")
    private BigDecimal receivedAmount;

    @Schema(description = "未收金额")
    private BigDecimal unpaidAmount;

    @Schema(description = "应收日期")
    private LocalDate receivableDate;

    @Schema(description = "到期日期")
    private LocalDate dueDate;

    @Schema(description = "账龄（天）")
    private Integer agingDays;

    @Schema(description = "单据状态（UNPAID-未收 PARTIAL-部分收款 PAID-已收 CANCELLED-已取消）")
    private String status;

    @Schema(description = "备注")
    private String remark;
}

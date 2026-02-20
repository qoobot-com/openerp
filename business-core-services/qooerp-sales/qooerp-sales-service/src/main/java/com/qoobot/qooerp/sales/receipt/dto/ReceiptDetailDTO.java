package com.qoobot.qooerp.sales.receipt.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 销售收款明细 DTO
 */
@Data
@Schema(description = "销售收款明细DTO")
public class ReceiptDetailDTO {

    @Schema(description = "明细ID")
    private Long id;

    @Schema(description = "收款单ID")
    private Long receiptId;

    @Schema(description = "订单ID")
    private Long orderId;

    @Schema(description = "订单编号")
    private String orderNo;

    @Schema(description = "订单金额")
    private BigDecimal orderAmount;

    @Schema(description = "已收金额")
    private BigDecimal paidAmount;

    @Schema(description = "本次核销金额")
    private BigDecimal writeoffAmount;

    @Schema(description = "备注")
    private String remark;
}

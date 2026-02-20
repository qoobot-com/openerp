package com.qoobot.qooerp.sales.report.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 销售趋势 DTO
 */
@Data
@Schema(description = "销售趋势DTO")
public class SalesTrendDTO {

    @Schema(description = "日期")
    private String date;

    @Schema(description = "订单数量")
    private Long orderCount;

    @Schema(description = "订单金额")
    private BigDecimal orderAmount;

    @Schema(description = "收款金额")
    private BigDecimal paidAmount;
}

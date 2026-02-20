package com.qoobot.qooerp.sales.report.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 销售统计 DTO
 */
@Data
@Schema(description = "销售统计DTO")
public class SalesStatisticsDTO {

    @Schema(description = "订单总数")
    private Long totalOrders;

    @Schema(description = "已取消订单数")
    private Long cancelledOrders;

    @Schema(description = "已完成订单数")
    private Long completedOrders;

    @Schema(description = "订单总金额")
    private BigDecimal totalAmount;

    @Schema(description = "已收金额")
    private BigDecimal paidAmount;

    @Schema(description = "未收金额")
    private BigDecimal unpaidAmount;

    @Schema(description = "退款金额")
    private BigDecimal refundAmount;

    @Schema(description = "平均订单金额")
    private BigDecimal avgOrderAmount;

    @Schema(description = "订单完成率")
    private BigDecimal completionRate;

    @Schema(description = "收款率")
    private BigDecimal paymentRate;
}

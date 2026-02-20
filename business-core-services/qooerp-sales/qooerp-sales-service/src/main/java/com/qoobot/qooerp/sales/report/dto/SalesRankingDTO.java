package com.qoobot.qooerp.sales.report.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 销售排行 DTO
 */
@Data
@Schema(description = "销售排行DTO")
public class SalesRankingDTO {

    @Schema(description = "排名")
    private Integer rank;

    @Schema(description = "ID")
    private Long id;

    @Schema(description = "名称")
    private String name;

    @Schema(description = "订单数量")
    private Long orderCount;

    @Schema(description = "订单金额")
    private BigDecimal orderAmount;
}

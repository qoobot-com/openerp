package com.qoobot.qooerp.sales.delivery.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 销售发货明细 DTO
 */
@Data
@Schema(description = "销售发货明细DTO")
public class DeliveryDetailDTO {

    @Schema(description = "明细ID")
    private Long id;

    @Schema(description = "发货单ID")
    private Long deliveryId;

    @Schema(description = "订单明细ID")
    private Long orderDetailId;

    @Schema(description = "商品ID")
    private Long productId;

    @Schema(description = "商品名称")
    private String productName;

    @Schema(description = "规格型号")
    private String specification;

    @Schema(description = "单位")
    private String unit;

    @Schema(description = "发货数量")
    private BigDecimal quantity;

    @Schema(description = "备注")
    private String remark;
}

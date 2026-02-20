package com.qoobot.qooerp.sales.response.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 销售退货明细 DTO
 */
@Data
@Schema(description = "销售退货明细DTO")
public class ReturnDetailDTO {

    @Schema(description = "明细ID")
    private Long id;

    @Schema(description = "退货单ID")
    private Long returnId;

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

    @Schema(description = "退货数量")
    private BigDecimal quantity;

    @Schema(description = "单价")
    private BigDecimal unitPrice;

    @Schema(description = "退货金额")
    private BigDecimal amount;

    @Schema(description = "备注")
    private String remark;
}

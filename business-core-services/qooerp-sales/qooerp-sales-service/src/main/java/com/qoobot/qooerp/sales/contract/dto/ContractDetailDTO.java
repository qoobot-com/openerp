package com.qoobot.qooerp.sales.contract.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 销售合同明细 DTO
 */
@Data
@Schema(description = "销售合同明细DTO")
public class ContractDetailDTO {

    @Schema(description = "明细ID")
    private Long id;

    @Schema(description = "合同ID")
    private Long contractId;

    @Schema(description = "商品ID")
    private Long productId;

    @Schema(description = "商品名称")
    private String productName;

    @Schema(description = "商品编码")
    private String productCode;

    @Schema(description = "规格型号")
    private String specification;

    @Schema(description = "单位")
    private String unit;

    @Schema(description = "数量")
    private BigDecimal quantity;

    @Schema(description = "单价")
    private BigDecimal unitPrice;

    @Schema(description = "折扣率")
    private BigDecimal discountRate;

    @Schema(description = "折扣金额")
    private BigDecimal discountAmount;

    @Schema(description = "金额")
    private BigDecimal amount;

    @Schema(description = "备注")
    private String remark;
}

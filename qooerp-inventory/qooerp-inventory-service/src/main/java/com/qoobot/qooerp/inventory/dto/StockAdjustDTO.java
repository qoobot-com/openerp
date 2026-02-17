package com.qoobot.qooerp.inventory.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class StockAdjustDTO {
    @NotNull(message = "库存ID不能为空")
    private Long stockId;

    @NotNull(message = "调整类型不能为空")
    @NotBlank(message = "调整类型不能为空")
    private String adjustType;

    @NotNull(message = "调整数量不能为空")
    private BigDecimal adjustQuantity;

    private String adjustReason;
    private String batchNo;
}

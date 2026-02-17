package com.qoobot.qooerp.inventory.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CheckDetailCreateDTO {
    @NotNull(message = "物料不能为空")
    private Long materialId;

    @NotNull(message = "实际数量不能为空")
    private BigDecimal actualQuantity;

    private String batchNo;
}

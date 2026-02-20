package com.qoobot.qooerp.inventory.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class InboundDetailCreateDTO {
    @NotNull(message = "物料不能为空")
    private Long materialId;

    @NotNull(message = "数量不能为空")
    private BigDecimal quantity;

    private BigDecimal unitPrice;
    private String batchNo;
    private LocalDateTime productionDate;
    private LocalDateTime expiryDate;
}

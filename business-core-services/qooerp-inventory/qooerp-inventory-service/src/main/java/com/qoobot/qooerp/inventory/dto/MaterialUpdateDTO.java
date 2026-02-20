package com.qoobot.qooerp.inventory.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class MaterialUpdateDTO {
    @NotNull(message = "物料ID不能为空")
    private Long id;

    private String materialName;
    private String materialSpec;
    private String materialUnit;
    private Long categoryId;
    private BigDecimal safetyStock;
    private BigDecimal maxStock;
    private String materialStatus;
    private String barcode;
    private String description;
}

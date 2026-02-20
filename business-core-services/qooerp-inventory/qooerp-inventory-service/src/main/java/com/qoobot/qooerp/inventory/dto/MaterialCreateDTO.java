package com.qoobot.qooerp.inventory.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class MaterialCreateDTO {
    @NotBlank(message = "物料编码不能为空")
    private String materialCode;

    @NotBlank(message = "物料名称不能为空")
    private String materialName;

    private String materialSpec;

    @NotBlank(message = "物料单位不能为空")
    private String materialUnit;

    @NotNull(message = "物料分类不能为空")
    private Long categoryId;

    private BigDecimal safetyStock;
    private BigDecimal maxStock;
    private String barcode;
    private String description;
}

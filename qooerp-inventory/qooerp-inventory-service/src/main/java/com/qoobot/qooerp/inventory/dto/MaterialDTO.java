package com.qoobot.qooerp.inventory.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class MaterialDTO {
    private Long id;
    private String materialCode;
    private String materialName;
    private String materialSpec;
    private String materialUnit;
    private Long categoryId;
    private String categoryName;
    private BigDecimal safetyStock;
    private BigDecimal maxStock;
    private String materialStatus;
    private String barcode;
    private String description;
    private LocalDateTime createTime;
}

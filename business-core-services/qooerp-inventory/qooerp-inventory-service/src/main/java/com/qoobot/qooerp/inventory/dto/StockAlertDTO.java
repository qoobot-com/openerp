package com.qoobot.qooerp.inventory.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class StockAlertDTO {
    private Long materialId;
    private String materialCode;
    private String materialName;
    private BigDecimal currentStock;
    private BigDecimal safetyStock;
    private BigDecimal maxStock;
    private String alertType;
    private String alertMessage;
}

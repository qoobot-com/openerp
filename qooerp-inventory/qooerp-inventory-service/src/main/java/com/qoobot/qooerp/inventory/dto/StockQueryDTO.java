package com.qoobot.qooerp.inventory.dto;

import lombok.Data;

@Data
public class StockQueryDTO {
    private Long warehouseId;
    private Long materialId;
    private String materialCode;
    private String materialName;
    private String stockStatus;
    private Integer current = 1;
    private Integer size = 10;
}

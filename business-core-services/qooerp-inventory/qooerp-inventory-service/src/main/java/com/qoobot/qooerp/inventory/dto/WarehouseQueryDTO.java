package com.qoobot.qooerp.inventory.dto;

import lombok.Data;

@Data
public class WarehouseQueryDTO {
    private String warehouseCode;
    private String warehouseName;
    private String warehouseType;
    private String warehouseStatus;
    private Integer current = 1;
    private Integer size = 10;
}

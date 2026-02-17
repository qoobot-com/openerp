package com.qoobot.qooerp.inventory.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class WarehouseDTO {
    private Long id;
    private String warehouseCode;
    private String warehouseName;
    private String warehouseType;
    private String province;
    private String city;
    private String district;
    private String address;
    private String contactPerson;
    private String contactPhone;
    private String warehouseStatus;
    private String description;
    private LocalDateTime createTime;
}

package com.qoobot.qooerp.inventory.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class WarehouseCreateDTO {
    @NotBlank(message = "仓库编码不能为空")
    private String warehouseCode;

    @NotBlank(message = "仓库名称不能为空")
    private String warehouseName;

    private String warehouseType;
    private String province;
    private String city;
    private String district;
    private String address;
    private String contactPerson;
    private String contactPhone;
    private String description;
}

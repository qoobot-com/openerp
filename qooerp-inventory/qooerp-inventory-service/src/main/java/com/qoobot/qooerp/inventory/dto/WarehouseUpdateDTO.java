package com.qoobot.qooerp.inventory.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class WarehouseUpdateDTO {
    @NotNull(message = "仓库ID不能为空")
    private Long id;

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
}

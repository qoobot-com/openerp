package com.qoobot.qooerp.inventory.dto;

import lombok.Data;

@Data
public class MaterialQueryDTO {
    private String materialCode;
    private String materialName;
    private Long categoryId;
    private String materialStatus;
    private Integer current = 1;
    private Integer size = 10;
}

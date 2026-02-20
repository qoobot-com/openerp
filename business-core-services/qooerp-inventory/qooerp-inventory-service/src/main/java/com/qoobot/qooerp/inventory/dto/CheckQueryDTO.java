package com.qoobot.qooerp.inventory.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CheckQueryDTO {
    private String checkNo;
    private String checkType;
    private Long warehouseId;
    private String checkStatus;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer current = 1;
    private Integer size = 10;
}

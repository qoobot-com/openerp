package com.qoobot.qooerp.inventory.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CheckDTO {
    private Long id;
    private String checkNo;
    private String checkType;
    private Long warehouseId;
    private String warehouseName;
    private String checkStatus;
    private LocalDateTime checkDate;
    private Integer totalItems;
    private Integer diffItems;
    private String operatorName;
    private LocalDateTime auditTime;
    private List<CheckDetailDTO> details;
}

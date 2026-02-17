package com.qoobot.qooerp.production.vo;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ProductionEquipmentVO {
    private Long id;
    private String equipmentCode;
    private String equipmentName;
    private String equipmentType;
    private Long workshopId;
    private String workshopName;
    private String specification;
    private String manufacturer;
    private LocalDate purchaseDate;
    private String status;
    private String statusDesc;
    private String remark;
    private Long tenantId;
    private String createdBy;
    private LocalDateTime createdTime;
    private String updatedBy;
    private LocalDateTime updatedTime;
}

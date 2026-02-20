package com.qoobot.qooerp.production.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ProductionMaterialVO {
    private Long id;
    private String materialNo;
    private Long orderId;
    private String orderNo;
    private Long materialId;
    private String materialName;
    private String materialCode;
    private BigDecimal requiredQuantity;
    private BigDecimal issuedQuantity;
    private BigDecimal issueRate;
    private Long warehouseId;
    private String warehouseName;
    private LocalDate issueDate;
    private String status;
    private String statusDesc;
    private String remark;
    private Long tenantId;
    private String createdBy;
    private LocalDateTime createdTime;
    private String updatedBy;
    private LocalDateTime updatedTime;
}

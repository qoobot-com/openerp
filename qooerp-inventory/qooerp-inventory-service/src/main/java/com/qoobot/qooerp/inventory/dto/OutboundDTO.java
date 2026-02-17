package com.qoobot.qooerp.inventory.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OutboundDTO {
    private Long id;
    private String outboundNo;
    private String outboundType;
    private String targetType;
    private Long targetId;
    private Long warehouseId;
    private String warehouseName;
    private Long customerId;
    private String customerName;
    private String outboundStatus;
    private BigDecimal totalQuantity;
    private BigDecimal totalAmount;
    private LocalDateTime outboundDate;
    private String operatorName;
    private LocalDateTime auditTime;
    private List<OutboundDetailDTO> details;
}

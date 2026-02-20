package com.qoobot.qooerp.purchase.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ReturnDTO {

    private Long id;

    private String returnCode;

    private Long orderId;

    private String orderCode;

    private Long receiptId;

    private Long supplierId;

    private String supplierName;

    private String returnDate;

    private BigDecimal returnAmount;

    private String reason;

    private String returnStatus;

    private String returnStatusName;

    private Long warehouseId;

    private String warehouseName;

    private Long operatorId;

    private String operatorName;

    private String remark;

    private LocalDateTime createTime;

    private List<ReturnDetailDTO> details;
}

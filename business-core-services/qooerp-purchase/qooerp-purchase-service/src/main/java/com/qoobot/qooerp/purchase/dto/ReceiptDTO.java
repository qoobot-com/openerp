package com.qoobot.qooerp.purchase.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ReceiptDTO {

    private Long id;

    private String receiptCode;

    private Long orderId;

    private String orderCode;

    private Long supplierId;

    private String supplierName;

    private String receiptDate;

    private BigDecimal receiptAmount;

    private String receiptStatus;

    private String receiptStatusName;

    private Long warehouseId;

    private String warehouseName;

    private Long operatorId;

    private String operatorName;

    private String remark;

    private LocalDateTime createTime;

    private List<ReceiptDetailDTO> details;
}

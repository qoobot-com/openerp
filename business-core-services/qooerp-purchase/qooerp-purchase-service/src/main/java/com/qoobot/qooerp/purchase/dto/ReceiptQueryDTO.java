package com.qoobot.qooerp.purchase.dto;

import lombok.Data;

@Data
public class ReceiptQueryDTO {

    private String receiptCode;

    private Long orderId;

    private Long supplierId;

    private String receiptStatus;

    private String startDate;

    private String endDate;

    private Integer pageNum = 1;

    private Integer pageSize = 10;
}

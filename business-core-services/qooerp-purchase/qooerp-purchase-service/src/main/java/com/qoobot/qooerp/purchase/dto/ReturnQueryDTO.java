package com.qoobot.qooerp.purchase.dto;

import lombok.Data;

@Data
public class ReturnQueryDTO {

    private String returnCode;

    private Long orderId;

    private Long supplierId;

    private String returnStatus;

    private String startDate;

    private String endDate;

    private Integer pageNum = 1;

    private Integer pageSize = 10;
}

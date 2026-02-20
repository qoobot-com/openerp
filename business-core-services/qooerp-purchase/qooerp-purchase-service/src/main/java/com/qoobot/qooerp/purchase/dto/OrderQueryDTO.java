package com.qoobot.qooerp.purchase.dto;

import lombok.Data;

@Data
public class OrderQueryDTO {

    private String orderCode;

    private Long supplierId;

    private String orderStatus;

    private String startDate;

    private String endDate;

    private Integer pageNum = 1;

    private Integer pageSize = 10;
}

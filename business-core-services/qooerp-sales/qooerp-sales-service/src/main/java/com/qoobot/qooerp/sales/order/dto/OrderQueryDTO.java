package com.qoobot.qooerp.sales.order.dto;

import lombok.Data;

import java.time.LocalDate;

/**
 * 订单查询DTO
 */
@Data
public class OrderQueryDTO {

    private Long customerId;
    private String customerName;
    private String orderStatus;
    private String orderType;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}

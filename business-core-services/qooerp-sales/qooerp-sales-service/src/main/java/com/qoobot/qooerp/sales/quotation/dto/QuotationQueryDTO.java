package com.qoobot.qooerp.sales.quotation.dto;

import lombok.Data;

import java.time.LocalDate;

/**
 * 报价查询DTO
 */
@Data
public class QuotationQueryDTO {

    private Long customerId;
    private String customerName;
    private String quotationStatus;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}

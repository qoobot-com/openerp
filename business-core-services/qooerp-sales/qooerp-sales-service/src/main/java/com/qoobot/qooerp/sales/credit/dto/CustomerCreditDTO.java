package com.qoobot.qooerp.sales.credit.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 客户信用DTO
 */
@Data
public class CustomerCreditDTO {

    private Long id;
    private Long customerId;
    private String customerName;
    private BigDecimal creditLimit;
    private BigDecimal creditUsed;
    private BigDecimal creditAvailable;
    private String creditLevel;
    private String creditStatus;
    private String remark;
}

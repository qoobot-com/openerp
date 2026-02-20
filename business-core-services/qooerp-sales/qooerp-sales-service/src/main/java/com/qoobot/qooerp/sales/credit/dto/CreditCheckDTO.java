package com.qoobot.qooerp.sales.credit.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 信用检查DTO
 */
@Data
public class CreditCheckDTO {

    private Long customerId;
    private String customerName;
    private BigDecimal creditLimit;
    private BigDecimal creditUsed;
    private BigDecimal creditAvailable;
    private BigDecimal requestAmount;
    private Boolean isAvailable;
    private String message;
}

package com.qoobot.qooerp.sales.quotation.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 报价DTO
 */
@Data
public class QuotationDTO {

    private Long id;
    private String quotationCode;
    private Long customerId;
    private String customerName;
    private LocalDate quotationDate;
    private LocalDate validUntil;
    private BigDecimal quotationAmount;
    private BigDecimal discountAmount;
    private BigDecimal payableAmount;
    private String quotationStatus;
    private Long salespersonId;
    private String salespersonName;
    private String remark;
    private List<QuotationDetailDTO> details;
}

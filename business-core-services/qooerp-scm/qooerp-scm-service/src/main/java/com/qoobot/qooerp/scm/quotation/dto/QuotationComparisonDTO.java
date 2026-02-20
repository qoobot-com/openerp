package com.qoobot.qooerp.scm.quotation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 报价对比DTO
 */
@Data
@Schema(description = "报价对比DTO")
public class QuotationComparisonDTO {
    
    @Schema(description = "物料编码")
    private String materialCode;
    
    @Schema(description = "物料名称")
    private String materialName;
    
    @Schema(description = "供应商报价列表")
    private java.util.List<SupplierQuotationInfoDTO> quotations;
    
    @Schema(description = "最优供应商ID")
    private Long bestSupplierId;
    
    @Schema(description = "最优供应商名称")
    private String bestSupplierName;
    
    @Schema(description = "最优单价")
    private BigDecimal bestUnitPrice;
    
    /**
     * 供应商报价信息DTO
     */
    @Data
    @Schema(description = "供应商报价信息DTO")
    public static class SupplierQuotationInfoDTO {
        
        @Schema(description = "供应商ID")
        private Long supplierId;
        
        @Schema(description = "供应商名称")
        private String supplierName;
        
        @Schema(description = "报价单号")
        private String quotationCode;
        
        @Schema(description = "单价")
        private BigDecimal unitPrice;
        
        @Schema(description = "交货期（天）")
        private Integer deliveryDays;
        
        @Schema(description = "报价日期")
        private LocalDate quotationDate;
        
        @Schema(description = "报价有效期")
        private LocalDate validUntil;
    }
}

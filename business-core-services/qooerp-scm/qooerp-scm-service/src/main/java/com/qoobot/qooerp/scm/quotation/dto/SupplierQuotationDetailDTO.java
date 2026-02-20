package com.qoobot.qooerp.scm.quotation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;

/**
 * 供应商报价明细DTO
 */
@Data
@Schema(description = "供应商报价明细DTO")
public class SupplierQuotationDetailDTO {
    
    @Schema(description = "明细ID")
    private Long id;
    
    @Schema(description = "报价单ID")
    private Long quotationId;
    
    @Schema(description = "物料编码")
    private String materialCode;
    
    @Schema(description = "物料名称")
    private String materialName;
    
    @Schema(description = "规格型号")
    private String specification;
    
    @Schema(description = "单位")
    private String unit;
    
    @Schema(description = "数量")
    private BigDecimal quantity;
    
    @Schema(description = "单价")
    private BigDecimal unitPrice;
    
    @Schema(description = "金额")
    private BigDecimal amount;
    
    @Schema(description = "交货期（天）")
    private Integer deliveryDays;
    
    @Schema(description = "备注")
    private String remark;
}

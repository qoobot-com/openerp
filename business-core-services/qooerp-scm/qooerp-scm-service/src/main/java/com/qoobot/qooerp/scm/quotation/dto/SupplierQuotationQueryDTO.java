package com.qoobot.qooerp.scm.quotation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDate;

/**
 * 供应商报价查询DTO
 */
@Data
@Schema(description = "供应商报价查询DTO")
public class SupplierQuotationQueryDTO {
    
    @Schema(description = "报价单号")
    private String quotationCode;
    
    @Schema(description = "供应商ID")
    private Long supplierId;
    
    @Schema(description = "供应商名称")
    private String supplierName;
    
    @Schema(description = "审核状态")
    private String auditStatus;
    
    @Schema(description = "是否转换")
    private Integer converted;
    
    @Schema(description = "起始日期")
    private LocalDate startDate;
    
    @Schema(description = "结束日期")
    private LocalDate endDate;
    
    @Schema(description = "当前页码")
    private Integer current = 1;
    
    @Schema(description = "每页条数")
    private Integer size = 10;
}

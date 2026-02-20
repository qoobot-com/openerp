package com.qoobot.qooerp.scm.quotation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 供应商报价单DTO
 */
@Data
@Schema(description = "供应商报价单DTO")
public class SupplierQuotationDTO {
    
    @Schema(description = "报价单ID")
    private Long id;
    
    @Schema(description = "报价单号")
    private String quotationCode;
    
    @Schema(description = "供应商ID")
    private Long supplierId;
    
    @Schema(description = "供应商名称")
    private String supplierName;
    
    @Schema(description = "报价日期")
    private LocalDate quotationDate;
    
    @Schema(description = "报价有效期")
    private LocalDate validUntil;
    
    @Schema(description = "报价类型")
    private String quotationType;
    
    @Schema(description = "报价总金额")
    private BigDecimal totalAmount;
    
    @Schema(description = "审核状态")
    private String auditStatus;
    
    @Schema(description = "审核人")
    private String auditor;
    
    @Schema(description = "审核时间")
    private LocalDateTime auditTime;
    
    @Schema(description = "审核意见")
    private String auditRemark;
    
    @Schema(description = "是否转采购订单")
    private Integer converted;
    
    @Schema(description = "采购订单号")
    private String purchaseOrderCode;
    
    @Schema(description = "转换时间")
    private LocalDateTime convertTime;
    
    @Schema(description = "附件地址")
    private String attachmentUrl;
    
    @Schema(description = "备注")
    private String remark;
    
    @Schema(description = "报价明细")
    private List<SupplierQuotationDetailDTO> details;
}

package com.qoobot.qooerp.scm.statement.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 供应商对账单DTO
 */
@Data
@Schema(description = "供应商对账单DTO")
public class SupplierStatementDTO {
    
    @Schema(description = "对账单ID")
    private Long id;
    
    @Schema(description = "对账单号")
    private String statementCode;
    
    @Schema(description = "供应商ID")
    private Long supplierId;
    
    @Schema(description = "供应商名称")
    private String supplierName;
    
    @Schema(description = "对账类型")
    private String statementType;
    
    @Schema(description = "对账起始日期")
    private LocalDate startDate;
    
    @Schema(description = "对账结束日期")
    private LocalDate endDate;
    
    @Schema(description = "对账日期")
    private LocalDate statementDate;
    
    @Schema(description = "采购总金额")
    private BigDecimal purchaseAmount;
    
    @Schema(description = "退货总金额")
    private BigDecimal returnAmount;
    
    @Schema(description = "运费")
    private BigDecimal freightAmount;
    
    @Schema(description = "其他费用")
    private BigDecimal otherAmount;
    
    @Schema(description = "应付总额")
    private BigDecimal totalAmount;
    
    @Schema(description = "审核状态")
    private String auditStatus;
    
    @Schema(description = "审核人")
    private String auditor;
    
    @Schema(description = "审核时间")
    private LocalDateTime auditTime;
    
    @Schema(description = "审核意见")
    private String auditRemark;
    
    @Schema(description = "结算状态")
    private String settlementStatus;
    
    @Schema(description = "结算日期")
    private LocalDate settlementDate;
    
    @Schema(description = "结算方式")
    private String settlementMethod;
    
    @Schema(description = "备注")
    private String remark;
    
    @Schema(description = "对账明细")
    private List<SupplierStatementDetailDTO> details;
}

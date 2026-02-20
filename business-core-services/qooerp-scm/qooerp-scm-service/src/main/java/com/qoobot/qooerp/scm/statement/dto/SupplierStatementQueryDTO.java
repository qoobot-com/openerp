package com.qoobot.qooerp.scm.statement.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDate;

/**
 * 供应商对账查询DTO
 */
@Data
@Schema(description = "供应商对账查询DTO")
public class SupplierStatementQueryDTO {
    
    @Schema(description = "对账单号")
    private String statementCode;
    
    @Schema(description = "供应商ID")
    private Long supplierId;
    
    @Schema(description = "供应商名称")
    private String supplierName;
    
    @Schema(description = "对账类型")
    private String statementType;
    
    @Schema(description = "审核状态")
    private String auditStatus;
    
    @Schema(description = "结算状态")
    private String settlementStatus;
    
    @Schema(description = "对账起始日期")
    private LocalDate startDate;
    
    @Schema(description = "对账结束日期")
    private LocalDate endDate;
    
    @Schema(description = "当前页码")
    private Integer current = 1;
    
    @Schema(description = "每页条数")
    private Integer size = 10;
}

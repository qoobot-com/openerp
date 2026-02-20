package com.qoobot.qooerp.scm.statement.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 供应商对账明细DTO
 */
@Data
@Schema(description = "供应商对账明细DTO")
public class SupplierStatementDetailDTO {
    
    @Schema(description = "明细ID")
    private Long id;
    
    @Schema(description = "对账单ID")
    private Long statementId;
    
    @Schema(description = "单据类型")
    private String orderType;
    
    @Schema(description = "单据ID")
    private Long orderId;
    
    @Schema(description = "单据号")
    private String orderCode;
    
    @Schema(description = "物料编码")
    private String materialCode;
    
    @Schema(description = "物料名称")
    private String materialName;
    
    @Schema(description = "数量")
    private BigDecimal quantity;
    
    @Schema(description = "单价")
    private BigDecimal unitPrice;
    
    @Schema(description = "金额")
    private BigDecimal amount;
    
    @Schema(description = "单据日期")
    private LocalDate orderDate;
    
    @Schema(description = "备注")
    private String remark;
}

package com.qoobot.qooerp.scm.statement.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;

/**
 * 对账统计DTO
 */
@Data
@Schema(description = "对账统计DTO")
public class StatementStatisticsDTO {
    
    @Schema(description = "对账单总数")
    private Long totalStatements;
    
    @Schema(description = "待审核对账单数")
    private Long pendingStatements;
    
    @Schema(description = "待结算对账单数")
    private Long pendingSettlements;
    
    @Schema(description = "已结算总金额")
    private BigDecimal settledAmount;
    
    @Schema(description = "未结算总金额")
    private BigDecimal unsettledAmount;
    
    @Schema(description = "对账总金额")
    private BigDecimal totalAmount;
}

package com.qoobot.qooerp.production.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ProductionQualityDTO {
    @NotNull(message = "质检单号不能为空")
    private String qualityNo;
    @NotNull(message = "生产订单ID不能为空")
    private Long orderId;
    private Long reportId;
    @NotNull(message = "产品ID不能为空")
    private Long productId;
    @NotNull(message = "检验数量不能为空")
    private BigDecimal inspectionQuantity;
    @NotNull(message = "合格数量不能为空")
    private BigDecimal qualifiedQuantity;
    private BigDecimal rejectQuantity;
    private Long inspectorId;
    private String inspectorName;
    private LocalDate inspectionDate;
    private String remark;
}

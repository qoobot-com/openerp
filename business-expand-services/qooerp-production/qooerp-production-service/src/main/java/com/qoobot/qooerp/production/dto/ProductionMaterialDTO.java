package com.qoobot.qooerp.production.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ProductionMaterialDTO {
    @NotNull(message = "领料单号不能为空")
    private String materialNo;
    @NotNull(message = "生产订单ID不能为空")
    private Long orderId;
    @NotNull(message = "物料ID不能为空")
    private Long materialId;
    @NotNull(message = "需求数量不能为空")
    private BigDecimal requiredQuantity;
    private Long warehouseId;
    private LocalDate issueDate;
    private String remark;
}

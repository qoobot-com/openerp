package com.qoobot.qooerp.production.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ProductionReceiptDTO {
    @NotNull(message = "入库单号不能为空")
    private String receiptNo;
    @NotNull(message = "生产订单ID不能为空")
    private Long orderId;
    @NotNull(message = "产品ID不能为空")
    private Long productId;
    @NotNull(message = "入库数量不能为空")
    private BigDecimal receiptQuantity;
    private Long warehouseId;
    private LocalDate receiptDate;
    private Long qualityCheckId;
    private String remark;
}

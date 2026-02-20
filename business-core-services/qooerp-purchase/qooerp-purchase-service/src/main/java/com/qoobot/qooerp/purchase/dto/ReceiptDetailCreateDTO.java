package com.qoobot.qooerp.purchase.dto;

import lombok.Data;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ReceiptDetailCreateDTO {

    @NotNull(message = "订单明细ID不能为空")
    private Long orderDetailId;

    @NotNull(message = "物料ID不能为空")
    private Long materialId;

    private String materialCode;

    private String materialName;

    private String specification;

    private String unit;

    @NotNull(message = "数量不能为空")
    @DecimalMin(value = "0.01", message = "数量必须大于0")
    private BigDecimal quantity;

    @NotNull(message = "单价不能为空")
    @DecimalMin(value = "0.01", message = "单价必须大于0")
    private BigDecimal unitPrice;

    private String batchNo;

    private LocalDate productionDate;

    private LocalDate expiryDate;

    private String remark;
}

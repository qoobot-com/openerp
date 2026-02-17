package com.qoobot.qooerp.production.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 生产订单DTO
 */
@Data
public class ProductionOrderDTO {

    @NotNull(message = "订单编号不能为空")
    private String orderNo;

    private Long planId;

    private String planNo;

    @NotNull(message = "产品ID不能为空")
    private Long productId;

    @NotNull(message = "订单数量不能为空")
    private BigDecimal orderQuantity;

    private LocalDate startDate;

    private LocalDate endDate;

    private Long workshopId;

    private String remark;
}

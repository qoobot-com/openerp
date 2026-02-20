package com.qoobot.qooerp.purchase.dto;

import lombok.Data;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class OrderCreateDTO {

    @NotNull(message = "供应商ID不能为空")
    private Long supplierId;

    private String supplierName;

    @NotNull(message = "订单日期不能为空")
    private LocalDate orderDate;

    @NotNull(message = "交货日期不能为空")
    private LocalDate deliveryDate;

    private Long warehouseId;

    private String warehouseName;

    private String remark;

    @Valid
    @NotNull(message = "订单明细不能为空")
    private List<OrderDetailCreateDTO> details;
}

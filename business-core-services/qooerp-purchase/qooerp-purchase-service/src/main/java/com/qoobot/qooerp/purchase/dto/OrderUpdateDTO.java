package com.qoobot.qooerp.purchase.dto;

import lombok.Data;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Data
public class OrderUpdateDTO {

    @NotNull(message = "订单ID不能为空")
    private Long id;

    private LocalDate deliveryDate;

    private Long warehouseId;

    private String warehouseName;

    private String remark;

    @Valid
    private List<OrderDetailCreateDTO> details;
}

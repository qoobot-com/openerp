package com.qoobot.qooerp.purchase.dto;

import lombok.Data;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Data
public class ReturnCreateDTO {

    @NotNull(message = "订单ID不能为空")
    private Long orderId;

    private Long receiptId;

    @NotNull(message = "退货日期不能为空")
    private LocalDate returnDate;

    private String reason;

    private Long warehouseId;

    private String warehouseName;

    private String remark;

    @Valid
    @NotNull(message = "退货明细不能为空")
    private List<ReturnDetailCreateDTO> details;
}

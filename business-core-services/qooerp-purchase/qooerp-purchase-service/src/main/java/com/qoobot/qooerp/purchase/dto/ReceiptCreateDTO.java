package com.qoobot.qooerp.purchase.dto;

import lombok.Data;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Data
public class ReceiptCreateDTO {

    @NotNull(message = "订单ID不能为空")
    private Long orderId;

    @NotNull(message = "入库日期不能为空")
    private LocalDate receiptDate;

    private Long warehouseId;

    private String warehouseName;

    private String remark;

    @Valid
    @NotNull(message = "入库明细不能为空")
    private List<ReceiptDetailCreateDTO> details;
}

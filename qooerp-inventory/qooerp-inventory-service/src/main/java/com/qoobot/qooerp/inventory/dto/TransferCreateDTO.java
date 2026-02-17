package com.qoobot.qooerp.inventory.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class TransferCreateDTO {
    @NotNull(message = "调拨类型不能为空")
    private String transferType;

    @NotNull(message = "源仓库不能为空")
    private Long sourceWarehouseId;

    @NotNull(message = "目标仓库不能为空")
    private Long targetWarehouseId;

    @NotEmpty(message = "调拨明细不能为空")
    @Valid
    private List<TransferDetailCreateDTO> details;
}

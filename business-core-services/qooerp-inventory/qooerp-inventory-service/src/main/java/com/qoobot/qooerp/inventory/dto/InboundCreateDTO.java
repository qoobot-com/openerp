package com.qoobot.qooerp.inventory.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class InboundCreateDTO {
    @NotNull(message = "入库类型不能为空")
    private String inboundType;

    @NotNull(message = "仓库不能为空")
    private Long warehouseId;

    private Long supplierId;

    @NotEmpty(message = "入库明细不能为空")
    @Valid
    private List<InboundDetailCreateDTO> details;
}

package com.qoobot.qooerp.inventory.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class OutboundCreateDTO {
    @NotNull(message = "出库类型不能为空")
    private String outboundType;

    @NotNull(message = "仓库不能为空")
    private Long warehouseId;

    private Long customerId;

    @NotEmpty(message = "出库明细不能为空")
    @Valid
    private List<OutboundDetailCreateDTO> details;
}

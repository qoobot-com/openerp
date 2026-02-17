package com.qoobot.qooerp.inventory.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class CheckCreateDTO {
    @NotNull(message = "盘点类型不能为空")
    private String checkType;

    @NotNull(message = "仓库不能为空")
    private Long warehouseId;

    @NotEmpty(message = "盘点明细不能为空")
    @Valid
    private List<CheckDetailCreateDTO> details;
}

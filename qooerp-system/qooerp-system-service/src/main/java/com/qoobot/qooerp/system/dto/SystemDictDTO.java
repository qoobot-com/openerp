package com.qoobot.qooerp.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 字典DTO
 *
 * @author QooERP Team
 * @version 1.0.0
 */
@Data
@Schema(description = "字典DTO")
public class SystemDictDTO {

    @Schema(description = "字典ID")
    private Long id;

    @Schema(description = "字典名称", required = true)
    @NotBlank(message = "字典名称不能为空")
    private String dictName;

    @Schema(description = "字典编码", required = true)
    @NotBlank(message = "字典编码不能为空")
    private String dictCode;

    @Schema(description = "字典类型", required = true)
    @NotBlank(message = "字典类型不能为空")
    private String dictType;

    @Schema(description = "状态：0-禁用 1-启用")
    private Integer status;

    @Schema(description = "备注")
    private String remark;
}

package com.qoobot.qooerp.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 字典项DTO
 *
 * @author QooERP Team
 * @version 1.0.0
 */
@Data
@Schema(description = "字典项DTO")
public class SystemDictItemDTO {

    @Schema(description = "字典项ID")
    private Long id;

    @Schema(description = "字典ID", required = true)
    @NotNull(message = "字典ID不能为空")
    private Long dictId;

    @Schema(description = "字典项标签", required = true)
    @NotBlank(message = "字典项标签不能为空")
    private String itemLabel;

    @Schema(description = "字典项值", required = true)
    @NotBlank(message = "字典项值不能为空")
    private String itemValue;

    @Schema(description = "字典项编码", required = true)
    @NotBlank(message = "字典项编码不能为空")
    private String itemCode;

    @Schema(description = "排序号")
    private Integer sort;

    @Schema(description = "状态：0-禁用 1-启用")
    private Integer status;

    @Schema(description = "备注")
    private String remark;
}

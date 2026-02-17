package com.qoobot.qooerp.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 系统参数DTO
 *
 * @author QooERP Team
 * @version 1.0.0
 */
@Data
@Schema(description = "系统参数DTO")
public class SystemConfigDTO {

    @Schema(description = "参数ID")
    private Long id;

    @Schema(description = "参数名称", required = true)
    @NotBlank(message = "参数名称不能为空")
    private String configName;

    @Schema(description = "参数键", required = true)
    @NotBlank(message = "参数键不能为空")
    private String configKey;

    @Schema(description = "参数值")
    private String configValue;

    @Schema(description = "参数类型", required = true)
    @NotBlank(message = "参数类型不能为空")
    private String configType;

    @Schema(description = "是否系统参数：0-否 1-是")
    private Integer isSystem;

    @Schema(description = "备注")
    private String remark;
}

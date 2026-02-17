package com.qoobot.qooerp.notify.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

/**
 * 模板预览DTO
 */
@Data
@Schema(description = "模板预览DTO")
public class TemplatePreviewDTO {

    @Schema(description = "模板ID")
    @NotNull(message = "模板ID不能为空")
    private Long templateId;

    @Schema(description = "变量值")
    @NotNull(message = "变量值不能为空")
    private Map<String, Object> variables;
}

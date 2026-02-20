package com.qoobot.qooerp.notify.dto;

import com.qoobot.qooerp.notify.enums.TemplateTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

/**
 * 模板创建DTO
 */
@Data
@Schema(description = "模板创建DTO")
public class TemplateCreateDTO {

    @Schema(description = "模板编码")
    @NotBlank(message = "模板编码不能为空")
    private String templateCode;

    @Schema(description = "模板名称")
    @NotBlank(message = "模板名称不能为空")
    private String templateName;

    @Schema(description = "分类")
    private String category;

    @Schema(description = "类型")
    @NotNull(message = "类型不能为空")
    private TemplateTypeEnum type;

    @Schema(description = "主题")
    private String subject;

    @Schema(description = "内容")
    @NotBlank(message = "内容不能为空")
    private String content;

    @Schema(description = "变量定义")
    private Map<String, String> variables;
}

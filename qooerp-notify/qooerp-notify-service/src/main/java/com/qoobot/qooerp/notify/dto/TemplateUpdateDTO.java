package com.qoobot.qooerp.notify.dto;

import com.qoobot.qooerp.notify.enums.TemplateTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

/**
 * 模板更新DTO
 */
@Data
@Schema(description = "模板更新DTO")
public class TemplateUpdateDTO {

    @Schema(description = "模板ID")
    @NotNull(message = "模板ID不能为空")
    private Long id;

    @Schema(description = "模板名称")
    @NotBlank(message = "模板名称不能为空")
    private String templateName;

    @Schema(description = "分类")
    private String category;

    @Schema(description = "主题")
    private String subject;

    @Schema(description = "内容")
    @NotBlank(message = "内容不能为空")
    private String content;

    @Schema(description = "变量定义")
    private Map<String, String> variables;

    @Schema(description = "状态")
    private Integer status;
}

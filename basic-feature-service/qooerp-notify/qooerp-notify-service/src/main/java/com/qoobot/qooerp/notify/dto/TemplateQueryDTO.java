package com.qoobot.qooerp.notify.dto;

import com.qoobot.qooerp.notify.enums.TemplateTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 模板查询DTO
 */
@Data
@Schema(description = "模板查询DTO")
public class TemplateQueryDTO {

    @Schema(description = "当前页")
    private Long current = 1L;

    @Schema(description = "每页大小")
    private Long size = 10L;

    @Schema(description = "模板编码")
    private String templateCode;

    @Schema(description = "模板名称")
    private String templateName;

    @Schema(description = "分类")
    private String category;

    @Schema(description = "类型")
    private TemplateTypeEnum type;

    @Schema(description = "状态")
    private Integer status;
}

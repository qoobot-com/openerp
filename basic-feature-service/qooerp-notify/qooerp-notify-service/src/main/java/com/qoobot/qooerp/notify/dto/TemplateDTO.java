package com.qoobot.qooerp.notify.dto;

import com.qoobot.qooerp.notify.enums.TemplateTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 模板DTO
 */
@Data
@Schema(description = "模板DTO")
public class TemplateDTO {

    @Schema(description = "模板ID")
    private Long id;

    @Schema(description = "租户ID")
    private Long tenantId;

    @Schema(description = "模板编码")
    private String templateCode;

    @Schema(description = "模板名称")
    private String templateName;

    @Schema(description = "分类")
    private String category;

    @Schema(description = "类型")
    private TemplateTypeEnum type;

    @Schema(description = "主题")
    private String subject;

    @Schema(description = "内容")
    private String content;

    @Schema(description = "变量定义")
    private Map<String, String> variables;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}

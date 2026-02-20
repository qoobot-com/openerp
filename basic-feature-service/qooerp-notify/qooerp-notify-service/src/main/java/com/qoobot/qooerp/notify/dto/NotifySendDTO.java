package com.qoobot.qooerp.notify.dto;

import com.qoobot.qooerp.notify.enums.NotifyTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

/**
 * 通知发送DTO
 */
@Data
@Schema(description = "通知发送DTO")
public class NotifySendDTO {

    @Schema(description = "通知类型")
    @NotNull(message = "通知类型不能为空")
    private NotifyTypeEnum type;

    @Schema(description = "接收者(邮箱/手机号/设备ID等)")
    @NotBlank(message = "接收者不能为空")
    private String receiver;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "内容")
    private String content;

    @Schema(description = "模板ID")
    private Long templateId;

    @Schema(description = "模板变量")
    private Map<String, Object> variables;

    @Schema(description = "扩展参数")
    private Map<String, Object> extraParams;
}

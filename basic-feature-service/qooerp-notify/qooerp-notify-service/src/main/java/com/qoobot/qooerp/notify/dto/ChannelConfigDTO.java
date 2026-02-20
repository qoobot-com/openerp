package com.qoobot.qooerp.notify.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

/**
 * 渠道配置DTO
 */
@Data
@Schema(description = "渠道配置DTO")
public class ChannelConfigDTO {

    @Schema(description = "渠道类型")
    @NotNull(message = "渠道类型不能为空")
    private String channelType;

    @Schema(description = "配置项")
    @NotNull(message = "配置项不能为空")
    private Map<String, Object> config;
}

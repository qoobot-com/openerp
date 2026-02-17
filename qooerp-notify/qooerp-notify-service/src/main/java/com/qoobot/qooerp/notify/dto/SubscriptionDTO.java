package com.qoobot.qooerp.notify.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 订阅DTO
 */
@Data
@Schema(description = "订阅DTO")
public class SubscriptionDTO {

    @Schema(description = "用户ID")
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @Schema(description = "渠道类型")
    @NotBlank(message = "渠道类型不能为空")
    private String channelType;

    @Schema(description = "接收地址")
    private String receiver;
}

package com.qoobot.qooerp.notify.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 免打扰设置DTO
 */
@Data
@Schema(description = "免打扰设置DTO")
public class DndSettingDTO {

    @Schema(description = "用户ID")
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @Schema(description = "开始时间")
    @NotBlank(message = "开始时间不能为空")
    private String startTime;

    @Schema(description = "结束时间")
    @NotBlank(message = "结束时间不能为空")
    private String endTime;
}

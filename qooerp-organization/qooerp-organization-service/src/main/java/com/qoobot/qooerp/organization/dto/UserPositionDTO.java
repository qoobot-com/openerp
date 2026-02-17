package com.qoobot.qooerp.organization.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户岗位关联DTO
 */
@Data
@Schema(description = "用户岗位关联DTO")
public class UserPositionDTO {

    @Schema(description = "用户ID")
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @Schema(description = "岗位ID")
    @NotNull(message = "岗位ID不能为空")
    private Long positionId;

    @Schema(description = "是否主岗位")
    private Integer isPrimary;

    @Schema(description = "任命时间")
    private LocalDateTime appointTime;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "备注")
    private String remark;
}

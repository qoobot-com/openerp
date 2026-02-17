package com.qoobot.qooerp.permission.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 用户DTO
 */
@Data
@Schema(description = "用户DTO")
public class PermissionUserDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "用户ID", required = true)
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @Schema(description = "角色ID列表", required = true)
    @NotNull(message = "角色ID列表不能为空")
    private List<Long> roleIds;
}

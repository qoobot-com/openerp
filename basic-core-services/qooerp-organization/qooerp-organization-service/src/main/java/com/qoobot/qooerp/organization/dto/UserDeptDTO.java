package com.qoobot.qooerp.organization.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户部门关联DTO
 */
@Data
@Schema(description = "用户部门关联DTO")
public class UserDeptDTO {

    @Schema(description = "用户ID")
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @Schema(description = "部门ID")
    @NotNull(message = "部门ID不能为空")
    private Long deptId;

    @Schema(description = "是否主部门")
    private Integer isPrimary;

    @Schema(description = "加入时间")
    private LocalDateTime joinTime;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "备注")
    private String remark;
}

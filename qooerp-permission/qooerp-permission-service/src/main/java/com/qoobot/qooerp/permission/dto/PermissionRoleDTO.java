package com.qoobot.qooerp.permission.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 角色DTO
 */
@Data
@Schema(description = "角色DTO")
public class PermissionRoleDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "角色ID")
    private Long id;

    @Schema(description = "租户ID")
    private Long tenantId;

    @Schema(description = "角色名称", required = true)
    @NotBlank(message = "角色名称不能为空")
    private String roleName;

    @Schema(description = "角色编码", required = true)
    @NotBlank(message = "角色编码不能为空")
    private String roleCode;

    @Schema(description = "角色类型：1-系统角色 2-业务角色", required = true)
    @NotNull(message = "角色类型不能为空")
    private Integer roleType;

    @Schema(description = "数据权限范围：1-全部 2-部门 3-部门及子部门 4-本人 5-自定义", required = true)
    @NotNull(message = "数据权限范围不能为空")
    private Integer dataScope;

    @Schema(description = "自定义部门ID（data_scope=5时使用）")
    private String deptIds;

    @Schema(description = "状态：0-禁用 1-启用", required = true)
    @NotNull(message = "状态不能为空")
    private Integer status;

    @Schema(description = "排序号")
    private Integer sort;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "菜单ID列表")
    private List<Long> menuIds;
}

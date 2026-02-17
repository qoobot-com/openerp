package com.qoobot.qooerp.permission.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 菜单DTO
 */
@Data
@Schema(description = "菜单DTO")
public class PermissionMenuDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "菜单ID")
    private Long id;

    @Schema(description = "父菜单ID")
    private Long parentId;

    @Schema(description = "菜单名称", required = true)
    @NotBlank(message = "菜单名称不能为空")
    private String menuName;

    @Schema(description = "菜单类型：M-目录 C-菜单 F-按钮", required = true)
    @NotBlank(message = "菜单类型不能为空")
    private String menuType;

    @Schema(description = "路由路径")
    private String path;

    @Schema(description = "组件路径")
    private String component;

    @Schema(description = "权限标识")
    private String permission;

    @Schema(description = "图标")
    private String icon;

    @Schema(description = "排序号")
    private Integer sort;

    @Schema(description = "是否显示：0-隐藏 1-显示")
    private Integer visible;

    @Schema(description = "是否缓存：0-不缓存 1-缓存")
    private Integer isCache;

    @Schema(description = "是否外链：0-否 1-是")
    private Integer isFrame;

    @Schema(description = "备注")
    private String remark;
}

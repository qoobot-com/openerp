package com.qoobot.qooerp.permission.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 菜单VO
 */
@Data
@Schema(description = "菜单VO")
public class PermissionMenuVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "菜单ID")
    private Long id;

    @Schema(description = "父菜单ID")
    private Long parentId;

    @Schema(description = "菜单名称")
    private String menuName;

    @Schema(description = "菜单类型")
    private String menuType;

    @Schema(description = "菜单类型名称")
    private String menuTypeName;

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

    @Schema(description = "是否显示")
    private Integer visible;

    @Schema(description = "是否缓存")
    private Integer isCache;

    @Schema(description = "是否外链")
    private Integer isFrame;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "子菜单列表")
    private List<PermissionMenuVO> children;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "创建人")
    private String createBy;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "更新人")
    private String updateBy;
}

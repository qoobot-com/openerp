package com.qoobot.qooerp.permission.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 菜单树VO
 */
@Data
@Schema(description = "菜单树VO")
public class PermissionTreeVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "菜单ID")
    private Long id;

    @Schema(description = "菜单名称")
    private String label;

    @Schema(description = "父菜单ID")
    private Long parentId;

    @Schema(description = "菜单类型")
    private String menuType;

    @Schema(description = "是否选中")
    private Boolean checked;

    @Schema(description = "子菜单列表")
    private List<PermissionTreeVO> children;
}

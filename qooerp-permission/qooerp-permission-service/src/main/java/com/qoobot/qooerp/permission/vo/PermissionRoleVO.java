package com.qoobot.qooerp.permission.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 角色VO
 */
@Data
@Schema(description = "角色VO")
public class PermissionRoleVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "角色ID")
    private Long id;

    @Schema(description = "租户ID")
    private Long tenantId;

    @Schema(description = "角色名称")
    private String roleName;

    @Schema(description = "角色编码")
    private String roleCode;

    @Schema(description = "角色类型")
    private Integer roleType;

    @Schema(description = "角色类型名称")
    private String roleTypeName;

    @Schema(description = "数据权限范围")
    private Integer dataScope;

    @Schema(description = "数据权限范围名称")
    private String dataScopeName;

    @Schema(description = "自定义部门ID")
    private String deptIds;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "状态名称")
    private String statusName;

    @Schema(description = "排序号")
    private Integer sort;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "菜单ID列表")
    private List<Long> menuIds;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "创建人")
    private String createBy;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "更新人")
    private String updateBy;
}

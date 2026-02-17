package com.qoobot.qooerp.permission.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 菜单表实体
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("permission_menu")
public class PermissionRoleMenu implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 菜单ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 父菜单ID
     */
    private Long parentId;
    /**
     * 角色ID
     */
    private Long roleId;
    /**
     * 菜单ID
     */
    private Long menuId;


    /**
     * 菜单名称
     */
    private String menuName;

    /**
     * 菜单类型：M-目录 C-菜单 F-按钮
     */
    private String menuType;

    /**
     * 路由路径
     */
    private String path;

    /**
     * 组件路径
     */
    private String component;

    /**
     * 权限标识
     */
    private String permission;

    /**
     * 图标
     */
    private String icon;

    /**
     * 排序号
     */
    private Integer sort;

    /**
     * 是否显示：0-隐藏 1-显示
     */
    private Integer visible;

    /**
     * 是否缓存：0-不缓存 1-缓存
     */
    private Integer isCache;

    /**
     * 是否外链：0-否 1-是
     */
    private Integer isFrame;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private java.time.LocalDateTime createTime;

    /**
     * 创建人
     */
    @TableField(fill = FieldFill.INSERT)
    private String createBy;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private java.time.LocalDateTime updateTime;

    /**
     * 更新人
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updateBy;
}

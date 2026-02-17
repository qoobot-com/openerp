package com.qoobot.qooerp.permission.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 角色表实体
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("permission_role")
public class PermissionRole implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 角色ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 角色编码（唯一）
     */
    private String roleCode;

    /**
     * 角色类型：1-系统角色 2-业务角色
     */
    private Integer roleType;

    /**
     * 数据权限范围：1-全部 2-部门 3-部门及子部门 4-本人 5-自定义
     */
    private Integer dataScope;

    /**
     * 自定义部门ID（data_scope=5时使用）
     */
    private String deptIds;

    /**
     * 状态：0-禁用 1-启用
     */
    private Integer status;

    /**
     * 排序号
     */
    private Integer sort;

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

    /**
     * 删除标记：0-未删除 1-已删除
     */
    @TableLogic
    private Integer deleted;
}

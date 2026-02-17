package com.qoobot.qooerp.permission.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qoobot.qooerp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 数据权限规则实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("permission_data_rule")
public class PermissionDataRule extends BaseEntity {

    /**
     * 规则ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 角色ID
     */
    private Long roleId;

    /**
     * 数据范围：1-全部 2-部门 3-部门及子部门 4-本人 5-自定义
     */
    private Integer dataScope;

    /**
     * 自定义部门ID列表（逗号分隔）
     */
    private String deptIds;

    /**
     * 资源类型
     */
    private String resourceType;

    /**
     * 优先级
     */
    private Integer priority;

    /**
     * 描述
     */
    private String description;
}

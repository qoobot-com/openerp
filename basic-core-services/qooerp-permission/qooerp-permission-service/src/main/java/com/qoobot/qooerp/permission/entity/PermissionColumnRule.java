package com.qoobot.qooerp.permission.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qoobot.qooerp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 列级权限规则实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("permission_column_rule")
public class PermissionColumnRule extends BaseEntity {

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
     * 表名
     */
    private String tableName;

    /**
     * 列名
     */
    private String columnName;

    /**
     * 权限类型：VISIBLE-可见 HIDDEN-隐藏 MASK-脱敏
     */
    private String permissionType;

    /**
     * 脱敏类型：PHONE-手机号 EMAIL-邮箱 ID_CARD-身份证 CARD-银行卡 NAME-姓名 ADDRESS-地址
     */
    private String maskType;

    /**
     * 优先级
     */
    private Integer priority;

    /**
     * 描述
     */
    private String description;
}

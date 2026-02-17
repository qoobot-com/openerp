package com.qoobot.qooerp.permission.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qoobot.qooerp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * ABAC策略实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("permission_abac_policy")
public class PermissionAbacPolicy extends BaseEntity {

    /**
     * 策略ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 策略名称
     */
    private String policyName;

    /**
     * 策略表达式
     */
    private String policyExpression;

    /**
     * 资源类型
     */
    private String resourceType;

    /**
     * 操作类型
     */
    private String operationType;

    /**
     * 优先级
     */
    private Integer priority;

    /**
     * 状态：0-禁用 1-启用
     */
    private Integer status;

    /**
     * 描述
     */
    private String description;
}

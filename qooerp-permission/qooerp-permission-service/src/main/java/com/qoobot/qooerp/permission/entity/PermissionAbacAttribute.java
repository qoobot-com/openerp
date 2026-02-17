package com.qoobot.qooerp.permission.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qoobot.qooerp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * ABAC属性实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("permission_abac_attribute")
public class PermissionAbacAttribute extends BaseEntity {

    /**
     * 属性ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 属性名称
     */
    private String attributeName;

    /**
     * 属性类型：USER-用户属性 ENV-环境属性 RESOURCE-资源属性
     */
    private String attributeType;

    /**
     * 属性键
     */
    private String attributeKey;

    /**
     * 属性值来源
     */
    private String valueSource;

    /**
     * 描述
     */
    private String description;
}

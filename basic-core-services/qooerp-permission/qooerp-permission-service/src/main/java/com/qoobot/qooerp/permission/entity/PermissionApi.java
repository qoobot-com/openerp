package com.qoobot.qooerp.permission.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qoobot.qooerp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 接口权限实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("permission_api")
public class PermissionApi extends BaseEntity {

    /**
     * API ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * API名称
     */
    private String apiName;

    /**
     * API路径
     */
    private String apiPath;

    /**
     * 请求方法
     */
    private String httpMethod;

    /**
     * 权限标识
     */
    private String permission;

    /**
     * 描述
     */
    private String description;
}

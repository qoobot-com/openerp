package com.qoobot.qooerp.organization.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户岗位关联实体类
 */
@Data
@TableName("organization_user_position")
public class OrganizationUserPosition implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long tenantId;

    private Long userId;

    private Long positionId;

    private Integer isPrimary;

    private LocalDateTime appointTime;

    private LocalDateTime dismissTime;

    private Integer status;

    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT)
    private String createBy;
}

package com.qoobot.qooerp.organization.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qoobot.qooerp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 岗位实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("organization_position")
public class OrganizationPosition extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 岗位ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 岗位名称
     */
    private String positionName;

    /**
     * 岗位编码（唯一）
     */
    private String positionCode;

    /**
     * 岗位级别
     */
    private Integer positionLevel;

    /**
     * 所属部门ID
     */
    private Long deptId;

    /**
     * 状态：0-禁用 1-启用
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;
}

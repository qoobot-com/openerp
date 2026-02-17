package com.qoobot.qooerp.organization.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 公司实体类
 */
@Data
@TableName("organization_company")
public class OrganizationCompany implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long tenantId;

    private Long parentId;

    private String companyCode;

    private String companyName;

    private String companyNameEn;

    private Integer companyLevel;

    private String legalPerson;

    private String creditCode;

    private String address;

    private String phone;

    private String email;

    private String logo;

    private String description;

    private Integer sort;

    private Integer status;

    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT)
    private String createBy;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updateBy;
}

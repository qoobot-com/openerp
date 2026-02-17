package com.qoobot.qooerp.organization.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qoobot.qooerp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * 部门实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("organization_dept")
public class OrganizationDept extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 部门ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 父部门ID
     */
    private Long parentId;

    /**
     * 部门名称
     */
    private String deptName;

    /**
     * 部门编码（唯一）
     */
    private String deptCode;

    /**
     * 负责人ID
     */
    private Long leaderId;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 地址
     */
    private String address;

    /**
     * 排序号
     */
    private Integer sort;

    /**
     * 部门级别
     */
    private Integer deptLevel;

    /**
     * 状态：0-禁用 1-启用
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 树形结构子节点（非持久化字段）
     */
    @TableField(exist = false)
    private List<OrganizationDept> children = new ArrayList<>();

    /**
     * 是否有子节点（非持久化字段）
     */
    @TableField(exist = false)
    private Boolean hasChildren = false;

    /**
     * 员工数量（非持久化字段）
     */
    @TableField(exist = false)
    private Integer employeeCount = 0;
}

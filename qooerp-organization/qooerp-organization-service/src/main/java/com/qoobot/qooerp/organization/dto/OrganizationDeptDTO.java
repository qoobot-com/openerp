package com.qoobot.qooerp.organization.dto;

import com.qoobot.qooerp.common.dto.PageQueryDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 部门查询DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class OrganizationDeptDTO extends PageQueryDTO {

    /**
     * 部门ID
     */
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
     * 部门编码
     */
    private String deptCode;

    /**
     * 负责人ID
     */
    private Long leaderId;

    /**
     * 状态：0-禁用 1-启用
     */
    private Integer status;
}

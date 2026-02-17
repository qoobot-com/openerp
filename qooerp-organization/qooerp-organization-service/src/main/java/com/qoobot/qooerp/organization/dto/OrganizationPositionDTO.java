package com.qoobot.qooerp.organization.dto;

import com.qoobot.qooerp.common.dto.PageQueryDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 岗位查询DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class OrganizationPositionDTO extends PageQueryDTO {

    /**
     * 岗位ID
     */
    private Long id;

    /**
     * 岗位名称
     */
    private String positionName;

    /**
     * 岗位编码
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
}

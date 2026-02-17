package com.qoobot.qooerp.organization.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 岗位VO
 */
@Data
public class OrganizationPositionVO implements Serializable {

    private static final long serialVersionUID = 1L;

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
     * 所属部门名称
     */
    private String deptName;

    /**
     * 状态：0-禁用 1-启用
     */
    private Integer status;

    /**
     * 状态名称
     */
    private String statusName;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 创建人
     */
    private String createByName;

    /**
     * 更新人
     */
    private String updateByName;
}

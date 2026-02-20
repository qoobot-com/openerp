package com.qoobot.qooerp.project.risk.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 项目风险DTO
 *
 * @author QooERP Team
 * @since 2026-02-18
 */
@Data
public class ProjectRiskDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 风险ID
     */
    private Long id;

    /**
     * 项目ID
     */
    private Long projectId;

    /**
     * 项目名称
     */
    private String projectName;

    /**
     * 风险名称
     */
    private String riskName;

    /**
     * 风险类型
     */
    private String riskType;

    /**
     * 发生概率(1-低,2-中,3-高)
     */
    private Integer probability;

    /**
     * 发生概率名称
     */
    private String probabilityName;

    /**
     * 影响程度(1-低,2-中,3-高)
     */
    private Integer impact;

    /**
     * 影响程度名称
     */
    private String impactName;

    /**
     * 风险等级
     */
    private String riskLevel;

    /**
     * 状态(0-未处理,1-处理中,2-已解决,3-已关闭)
     */
    private Integer status;

    /**
     * 状态名称
     */
    private String statusName;

    /**
     * 应对策略
     */
    private String response;

    /**
     * 负责人ID
     */
    private Long ownerId;

    /**
     * 负责人姓名
     */
    private String ownerName;

    /**
     * 租户ID
     */
    private Long tenantId;

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
    private String createBy;

    /**
     * 更新人
     */
    private String updateBy;
}

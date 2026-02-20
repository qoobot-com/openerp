package com.qoobot.qooerp.project.risk.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 项目风险实体类
 *
 * @author QooERP Team
 * @since 2026-02-18
 */
@Data
@TableName("project_risk")
public class ProjectRisk implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 项目ID
     */
    private Long projectId;

    /**
     * 风险名称
     */
    private String riskName;

    /**
     * 风险类型(技术风险/进度风险/资源风险等)
     */
    private String riskType;

    /**
     * 发生概率(1-低,2-中,3-高)
     */
    private Integer probability;

    /**
     * 影响程度(1-低,2-中,3-高)
     */
    private Integer impact;

    /**
     * 状态(0-未处理,1-处理中,2-已解决,3-已关闭)
     */
    private Integer status;

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
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 创建人
     */
    @TableField(fill = FieldFill.INSERT)
    private String createBy;

    /**
     * 更新人
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updateBy;

    /**
     * 是否删除(0-否,1-是)
     */
    @TableLogic
    private Integer deleted;
}

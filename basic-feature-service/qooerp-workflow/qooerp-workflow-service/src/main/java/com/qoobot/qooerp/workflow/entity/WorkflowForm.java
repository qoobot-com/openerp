package com.qoobot.qooerp.workflow.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 流程表单表
 */
@Data
public class WorkflowForm implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 表单ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 表单名称
     */
    private String name;

    /**
     * 表单编码
     */
    private String code;

    /**
     * 表单版本
     */
    private Integer version;

    /**
     * 表单类型 (single-单表, master-detail-主子表)
     */
    private String formType;

    /**
     * 表单配置 (JSON)
     */
    private String formConfig;

    /**
     * 描述
     */
    private String description;

    /**
     * 状态 (0-草稿, 1-已发布, 2-已停用)
     */
    private Integer status;

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    /**
     * 创建人ID
     */
    @TableField(fill = FieldFill.INSERT)
    private String createBy;

    /**
     * 更新人ID
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updateBy;

    /**
     * 删除标志 (0-未删除, 1-已删除)
     */
    @TableLogic
    private Integer deleted;
}

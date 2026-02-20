package com.qoobot.qooerp.project.comment.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 项目评论实体类
 *
 * @author QooERP Team
 * @since 2026-02-18
 */
@Data
@TableName("project_comment")
public class ProjectComment implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 实体类型(project/task)
     */
    private String entityType;

    /**
     * 实体ID
     */
    private Long entityId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户姓名
     */
    private String userName;

    /**
     * 评论内容
     */
    private String content;

    /**
     * @提及的用户ID列表(逗号分隔)
     */
    private String mentionedUsers;

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
     * 是否删除(0-否,1-是)
     */
    @TableLogic
    private Integer deleted;
}

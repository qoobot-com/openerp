package com.qoobot.qooerp.project.comment.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 项目评论DTO
 *
 * @author QooERP Team
 * @since 2026-02-18
 */
@Data
public class ProjectCommentDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 评论ID
     */
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
     * @提及的用户ID列表
     */
    private java.util.List<Long> mentionedUsers;

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
}

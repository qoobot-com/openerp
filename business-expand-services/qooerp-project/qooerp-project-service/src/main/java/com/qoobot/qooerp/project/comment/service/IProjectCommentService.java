package com.qoobot.qooerp.project.comment.service;

import com.qoobot.qooerp.common.response.PageResult;
import com.qoobot.qooerp.project.comment.dto.ProjectCommentDTO;

import java.util.List;

/**
 * 项目评论服务接口
 *
 * @author QooERP Team
 * @since 2026-02-18
 */
public interface IProjectCommentService {

    /**
     * 创建评论
     */
    ProjectCommentDTO create(ProjectCommentDTO dto);

    /**
     * 更新评论
     */
    ProjectCommentDTO update(Long id, ProjectCommentDTO dto);

    /**
     * 删除评论
     */
    void delete(Long id);

    /**
     * 根据ID查询评论
     */
    ProjectCommentDTO getById(Long id);

    /**
     * 分页查询评论
     */
    PageResult<ProjectCommentDTO> page(String entityType, Long entityId, Long current, Long size, Long tenantId);

    /**
     * 根据实体类型和ID查询评论列表
     */
    List<ProjectCommentDTO> listByEntity(String entityType, Long entityId);
}

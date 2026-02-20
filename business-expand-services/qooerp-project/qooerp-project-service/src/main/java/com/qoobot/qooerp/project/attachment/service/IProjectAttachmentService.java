package com.qoobot.qooerp.project.attachment.service;

import com.qoobot.qooerp.common.response.PageResult;
import com.qoobot.qooerp.project.attachment.dto.ProjectAttachmentDTO;

import java.util.List;

/**
 * 项目附件服务接口
 *
 * @author QooERP Team
 * @since 2026-02-18
 */
public interface IProjectAttachmentService {

    /**
     * 上传附件
     */
    ProjectAttachmentDTO upload(ProjectAttachmentDTO dto);

    /**
     * 删除附件
     */
    void delete(Long id);

    /**
     * 根据ID查询附件
     */
    ProjectAttachmentDTO getById(Long id);

    /**
     * 分页查询附件
     */
    PageResult<ProjectAttachmentDTO> page(Long projectId, Long taskId, Long current, Long size, Long tenantId);

    /**
     * 根据项目ID查询附件列表
     */
    List<ProjectAttachmentDTO> listByProjectId(Long projectId);

    /**
     * 根据任务ID查询附件列表
     */
    List<ProjectAttachmentDTO> listByTaskId(Long taskId);
}

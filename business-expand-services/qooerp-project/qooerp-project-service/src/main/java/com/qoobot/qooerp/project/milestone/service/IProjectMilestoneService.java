package com.qoobot.qooerp.project.milestone.service;

import com.qoobot.qooerp.common.response.PageResult;
import com.qoobot.qooerp.project.milestone.dto.ProjectMilestoneDTO;

import java.util.List;

/**
 * 项目里程碑服务接口
 *
 * @author QooERP Team
 * @since 2026-02-18
 */
public interface IProjectMilestoneService {

    /**
     * 创建里程碑
     */
    ProjectMilestoneDTO create(ProjectMilestoneDTO dto);

    /**
     * 更新里程碑
     */
    ProjectMilestoneDTO update(Long id, ProjectMilestoneDTO dto);

    /**
     * 删除里程碑
     */
    void delete(Long id);

    /**
     * 根据ID查询里程碑
     */
    ProjectMilestoneDTO getById(Long id);

    /**
     * 分页查询里程碑
     */
    PageResult<ProjectMilestoneDTO> page(Long projectId, Long current, Long size, Long tenantId);

    /**
     * 完成里程碑
     */
    void complete(Long id);

    /**
     * 根据项目ID查询里程碑列表
     */
    List<ProjectMilestoneDTO> listByProjectId(Long projectId);
}

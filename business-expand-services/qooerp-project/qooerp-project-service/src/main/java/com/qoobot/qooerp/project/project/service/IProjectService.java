package com.qoobot.qooerp.project.project.service;

import com.qoobot.qooerp.common.response.PageResult;
import com.qoobot.qooerp.project.project.dto.ProjectDTO;
import com.qoobot.qooerp.project.project.dto.ProjectProgressDTO;
import com.qoobot.qooerp.project.project.dto.ProjectQueryDTO;

/**
 * 项目服务接口
 *
 * @author QooERP Team
 * @since 2026-02-18
 */
public interface IProjectService {

    /**
     * 创建项目
     */
    ProjectDTO create(ProjectDTO dto);

    /**
     * 更新项目
     */
    ProjectDTO update(Long id, ProjectDTO dto);

    /**
     * 删除项目
     */
    void delete(Long id);

    /**
     * 根据ID查询项目
     */
    ProjectDTO getById(Long id);

    /**
     * 分页查询项目
     */
    PageResult<ProjectDTO> page(ProjectQueryDTO queryDTO);

    /**
     * 审批项目
     */
    void approve(Long id);

    /**
     * 关闭项目
     */
    void close(Long id);

    /**
     * 启动项目
     */
    void start(Long id);

    /**
     * 获取项目进度
     */
    ProjectProgressDTO getProgress(Long id);

    /**
     * 计算项目进度
     */
    ProjectProgressDTO calculateProgress(Long projectId);

    /**
     * 根据项目编号查询
     */
    ProjectDTO getByProjectNo(String projectNo);
}

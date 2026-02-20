package com.qoobot.qooerp.project.task.service;

import com.qoobot.qooerp.common.response.PageResult;
import com.qoobot.qooerp.project.task.dto.ProjectTaskDTO;
import com.qoobot.qooerp.project.task.dto.ProjectTaskQueryDTO;

/**
 * 项目任务服务接口
 *
 * @author QooERP Team
 * @since 2026-02-18
 */
public interface IProjectTaskService {

    /**
     * 创建任务
     */
    ProjectTaskDTO create(ProjectTaskDTO dto);

    /**
     * 更新任务
     */
    ProjectTaskDTO update(Long id, ProjectTaskDTO dto);

    /**
     * 删除任务
     */
    void delete(Long id);

    /**
     * 根据ID查询任务
     */
    ProjectTaskDTO getById(Long id);

    /**
     * 分页查询任务
     */
    PageResult<ProjectTaskDTO> page(ProjectTaskQueryDTO queryDTO);

    /**
     * 分配任务
     */
    void assign(Long id, Long assigneeId, String assigneeName);

    /**
     * 开始任务
     */
    void start(Long id);

    /**
     * 完成任务
     */
    void complete(Long id);

    /**
     * 取消任务
     */
    void cancel(Long id);

    /**
     * 阻塞任务
     */
    void block(Long id);

    /**
     * 解除阻塞
     */
    void unblock(Long id);

    /**
     * 根据项目ID统计任务数
     */
    Long countByProjectId(Long projectId);

    /**
     * 根据状态和项目ID统计任务数
     */
    Long countByStatusAndProjectId(Integer status, Long projectId);

    /**
     * 统计逾期任务数
     */
    Long countOverdueByProjectId(Long projectId);

    /**
     * 根据负责人ID统计任务数
     */
    Long countByAssigneeId(Long assigneeId);

    /**
     * 根据项目ID查询任务列表
     */
    java.util.List<ProjectTaskDTO> listByProjectId(Long projectId);
}

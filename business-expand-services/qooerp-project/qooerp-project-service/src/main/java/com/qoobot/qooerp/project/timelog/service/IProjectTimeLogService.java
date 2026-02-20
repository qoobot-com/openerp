package com.qoobot.qooerp.project.timelog.service;

import com.qoobot.qooerp.common.response.PageResult;
import com.qoobot.qooerp.project.timelog.dto.ProjectTimeLogDTO;

import java.math.BigDecimal;
import java.util.List;

/**
 * 工时记录服务接口
 *
 * @author QooERP Team
 * @since 2026-02-18
 */
public interface IProjectTimeLogService {

    /**
     * 添加工时记录
     */
    ProjectTimeLogDTO create(ProjectTimeLogDTO dto);

    /**
     * 更新工时记录
     */
    ProjectTimeLogDTO update(Long id, ProjectTimeLogDTO dto);

    /**
     * 删除工时记录
     */
    void delete(Long id);

    /**
     * 根据ID查询工时记录
     */
    ProjectTimeLogDTO getById(Long id);

    /**
     * 分页查询工时记录
     */
    PageResult<ProjectTimeLogDTO> page(Long taskId, Long userId, Long current, Long size, Long tenantId);

    /**
     * 根据任务ID查询工时记录列表
     */
    List<ProjectTimeLogDTO> listByTaskId(Long taskId);

    /**
     * 根据用户ID查询工时记录列表
     */
    List<ProjectTimeLogDTO> listByUserId(Long userId);

    /**
     * 统计任务总工时
     */
    BigDecimal sumHoursByTaskId(Long taskId);

    /**
     * 统计用户总工时
     */
    BigDecimal sumHoursByUserId(Long userId);
}

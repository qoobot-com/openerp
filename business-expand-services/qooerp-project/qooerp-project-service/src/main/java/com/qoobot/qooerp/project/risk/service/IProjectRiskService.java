package com.qoobot.qooerp.project.risk.service;

import com.qoobot.qooerp.common.response.PageResult;
import com.qoobot.qooerp.project.risk.dto.ProjectRiskDTO;

import java.util.List;

/**
 * 项目风险服务接口
 *
 * @author QooERP Team
 * @since 2026-02-18
 */
public interface IProjectRiskService {

    /**
     * 创建风险
     */
    ProjectRiskDTO create(ProjectRiskDTO dto);

    /**
     * 更新风险
     */
    ProjectRiskDTO update(Long id, ProjectRiskDTO dto);

    /**
     * 删除风险
     */
    void delete(Long id);

    /**
     * 根据ID查询风险
     */
    ProjectRiskDTO getById(Long id);

    /**
     * 分页查询风险
     */
    PageResult<ProjectRiskDTO> page(Long projectId, Long current, Long size, Long tenantId);

    /**
     * 根据项目ID查询风险列表
     */
    List<ProjectRiskDTO> listByProjectId(Long projectId);

    /**
     * 更新风险状态
     */
    void updateStatus(Long id, Integer status);

    /**
     * 评估风险（计算风险等级）
     */
    void assessRisk(Long id);
}

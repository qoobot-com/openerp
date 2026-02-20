package com.qoobot.qooerp.project.milestone.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qoobot.qooerp.common.exception.BusinessException;
import com.qoobot.qooerp.common.response.PageResult;
import com.qoobot.qooerp.project.milestone.domain.ProjectMilestone;
import com.qoobot.qooerp.project.milestone.dto.ProjectMilestoneDTO;
import com.qoobot.qooerp.project.milestone.mapper.ProjectMilestoneMapper;
import com.qoobot.qooerp.project.milestone.service.IProjectMilestoneService;
import com.qoobot.qooerp.project.project.dto.ProjectDTO;
import com.qoobot.qooerp.project.project.service.IProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 项目里程碑服务实现类
 *
 * @author QooERP Team
 * @since 2026-02-18
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectMilestoneServiceImpl extends ServiceImpl<ProjectMilestoneMapper, ProjectMilestone>
        implements IProjectMilestoneService {

    private final IProjectService projectService;

    private static final int STATUS_NOT_COMPLETED = 0;
    private static final int STATUS_COMPLETED = 1;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProjectMilestoneDTO create(ProjectMilestoneDTO dto) {
        ProjectDTO project = projectService.getById(dto.getProjectId());
        if (project == null) {
            throw new BusinessException("项目不存在");
        }

        ProjectMilestone milestone = new ProjectMilestone();
        BeanUtils.copyProperties(dto, milestone);

        milestone.setMilestoneNo(generateMilestoneNo(dto.getProjectId()));
        milestone.setStatus(STATUS_NOT_COMPLETED);

        save(milestone);
        return toDTO(milestone);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProjectMilestoneDTO update(Long id, ProjectMilestoneDTO dto) {
        ProjectMilestone milestone = super.getById(id);
        if (milestone == null) {
            throw new BusinessException("里程碑不存在");
        }

        if (STATUS_COMPLETED == milestone.getStatus()) {
            throw new BusinessException("已完成的里程碑不能编辑");
        }

        BeanUtils.copyProperties(dto, milestone);
        milestone.setId(id);
        updateById(milestone);

        return toDTO(milestone);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        ProjectMilestone milestone = super.getById(id);
        if (milestone == null) {
            throw new BusinessException("里程碑不存在");
        }

        if (STATUS_COMPLETED == milestone.getStatus()) {
            throw new BusinessException("已完成的里程碑不能删除");
        }

        removeById(id);
    }

    @Override
    public ProjectMilestoneDTO getById(Long id) {
        ProjectMilestone milestone = super.getById(id);
        return toDTO(milestone);
    }

    @Override
    public PageResult<ProjectMilestoneDTO> page(Long projectId, Long current, Long size, Long tenantId) {
        LambdaQueryWrapper<ProjectMilestone> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProjectMilestone::getProjectId, projectId);
        wrapper.eq(ProjectMilestone::getTenantId, tenantId);
        wrapper.orderByAsc(ProjectMilestone::getTargetDate);

        Page<ProjectMilestone> page = new Page<>(current, size);
        IPage<ProjectMilestone> milestonePage = page(page, wrapper);

        return PageResult.of(
                milestonePage.getCurrent(),
                milestonePage.getSize(),
                milestonePage.getTotal(),
                milestonePage.getRecords().stream().map(this::toDTO).toList()
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void complete(Long id) {
        ProjectMilestone milestone = super.getById(id);
        if (milestone == null) {
            throw new BusinessException("里程碑不存在");
        }

        if (STATUS_COMPLETED == milestone.getStatus()) {
            throw new BusinessException("里程碑已完成");
        }

        milestone.setStatus(STATUS_COMPLETED);
        milestone.setActualDate(LocalDate.now());
        updateById(milestone);
    }

    @Override
    public List<ProjectMilestoneDTO> listByProjectId(Long projectId) {
        LambdaQueryWrapper<ProjectMilestone> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProjectMilestone::getProjectId, projectId);
        wrapper.orderByAsc(ProjectMilestone::getTargetDate);
        return list(wrapper).stream().map(this::toDTO).collect(Collectors.toList());
    }

    private String generateMilestoneNo(Long projectId) {
        long timestamp = System.currentTimeMillis();
        return "MILESTONE" + projectId + String.format("%04d", (int) (timestamp % 10000));
    }

    private ProjectMilestoneDTO toDTO(ProjectMilestone milestone) {
        if (milestone == null) {
            return null;
        }

        ProjectMilestoneDTO dto = new ProjectMilestoneDTO();
        BeanUtils.copyProperties(milestone, dto);
        dto.setStatusName(STATUS_COMPLETED == milestone.getStatus() ? "已达成" : "未达成");

        if (milestone.getProjectId() != null) {
            ProjectDTO project = projectService.getById(milestone.getProjectId());
            if (project != null) {
                dto.setProjectName(project.getProjectName());
            }
        }

        return dto;
    }
}

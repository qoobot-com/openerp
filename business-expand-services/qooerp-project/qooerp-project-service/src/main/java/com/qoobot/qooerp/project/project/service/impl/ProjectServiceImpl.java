package com.qoobot.qooerp.project.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qoobot.qooerp.common.exception.BusinessException;
import com.qoobot.qooerp.common.response.PageResult;
import com.qoobot.qooerp.project.project.domain.Project;
import com.qoobot.qooerp.project.project.dto.ProjectDTO;
import com.qoobot.qooerp.project.project.dto.ProjectProgressDTO;
import com.qoobot.qooerp.project.project.dto.ProjectQueryDTO;
import com.qoobot.qooerp.project.project.mapper.ProjectMapper;
import com.qoobot.qooerp.project.project.service.IProjectService;
import com.qoobot.qooerp.project.task.domain.ProjectTask;
import com.qoobot.qooerp.project.task.service.IProjectTaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

/**
 * 项目服务实现类
 *
 * @author QooERP Team
 * @since 2026-02-18
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectServiceImpl extends ServiceImpl<ProjectMapper, Project> implements IProjectService {

    private final IProjectTaskService taskService;

    private static final int STATUS_DRAFT = 0;
    private static final int STATUS_APPROVED = 1;
    private static final int STATUS_IN_PROGRESS = 2;
    private static final int STATUS_COMPLETED = 3;
    private static final int STATUS_CLOSED = 4;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProjectDTO create(ProjectDTO dto) {
        Project project = new Project();
        BeanUtils.copyProperties(dto, project);

        // 生成项目编号
        project.setProjectNo(generateProjectNo());
        project.setStatus(STATUS_DRAFT);

        save(project);

        return toDTO(project);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProjectDTO update(Long id, ProjectDTO dto) {
        Project project = super.getById(id);
        if (project == null) {
            throw new BusinessException("项目不存在");
        }

        // 已关闭的项目不能编辑
        if (STATUS_CLOSED == project.getStatus()) {
            throw new BusinessException("已关闭的项目不能编辑");
        }

        BeanUtils.copyProperties(dto, project);
        project.setId(id);
        updateById(project);

        return toDTO(project);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        Project project = super.getById(id);
        if (project == null) {
            throw new BusinessException("项目不存在");
        }

        // 已审批、进行中的项目不能删除
        if (STATUS_APPROVED == project.getStatus() || STATUS_IN_PROGRESS == project.getStatus()) {
            throw new BusinessException("已审批或进行中的项目不能删除");
        }

        removeById(id);
    }

    @Override
    public ProjectDTO getById(Long id) {
        Project project = super.getById(id);
        return toDTO(project);
    }

    @Override
    public PageResult<ProjectDTO> page(ProjectQueryDTO queryDTO) {
        LambdaQueryWrapper<Project> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(queryDTO.getProjectNo())) {
            wrapper.eq(Project::getProjectNo, queryDTO.getProjectNo());
        }

        if (StringUtils.hasText(queryDTO.getProjectName())) {
            wrapper.like(Project::getProjectName, queryDTO.getProjectName());
        }

        if (queryDTO.getStatus() != null) {
            wrapper.eq(Project::getStatus, queryDTO.getStatus());
        }

        if (queryDTO.getManagerId() != null) {
            wrapper.eq(Project::getManagerId, queryDTO.getManagerId());
        }

        if (queryDTO.getStartDateBegin() != null) {
            wrapper.ge(Project::getStartDate, queryDTO.getStartDateBegin());
        }

        if (queryDTO.getStartDateEnd() != null) {
            wrapper.le(Project::getStartDate, queryDTO.getStartDateEnd());
        }

        if (queryDTO.getEndDateBegin() != null) {
            wrapper.ge(Project::getEndDate, queryDTO.getEndDateBegin());
        }

        if (queryDTO.getEndDateEnd() != null) {
            wrapper.le(Project::getEndDate, queryDTO.getEndDateEnd());
        }

        if (queryDTO.getTenantId() != null) {
            wrapper.eq(Project::getTenantId, queryDTO.getTenantId());
        }

        wrapper.orderByDesc(Project::getCreateTime);

        Page<Project> page = new Page<>(queryDTO.getCurrent(), queryDTO.getSize());
        IPage<Project> projectPage = page(page, wrapper);

        return PageResult.of(
                projectPage.getCurrent(),
                projectPage.getSize(),
                projectPage.getTotal(),
                projectPage.getRecords().stream().map(this::toDTO).toList()
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approve(Long id) {
        Project project = super.getById(id);
        if (project == null) {
            throw new BusinessException("项目不存在");
        }

        if (STATUS_DRAFT != project.getStatus()) {
            throw new BusinessException("只有草稿状态的项目可以审批");
        }

        project.setStatus(STATUS_APPROVED);
        updateById(project);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void close(Long id) {
        Project project = super.getById(id);
        if (project == null) {
            throw new BusinessException("项目不存在");
        }

        if (STATUS_CLOSED == project.getStatus()) {
            throw new BusinessException("项目已关闭");
        }

        project.setStatus(STATUS_CLOSED);
        updateById(project);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void start(Long id) {
        Project project = super.getById(id);
        if (project == null) {
            throw new BusinessException("项目不存在");
        }

        if (STATUS_APPROVED != project.getStatus()) {
            throw new BusinessException("只有已审批的项目可以启动");
        }

        project.setStatus(STATUS_IN_PROGRESS);
        updateById(project);
    }

    @Override
    public ProjectProgressDTO getProgress(Long id) {
        return calculateProgress(id);
    }

    @Override
    public ProjectProgressDTO calculateProgress(Long projectId) {
        Project project = super.getById(projectId);
        if (project == null) {
            throw new BusinessException("项目不存在");
        }

        // 获取任务统计信息
        Long totalTasks = taskService.countByProjectId(projectId);
        Long completedTasks = taskService.countByStatusAndProjectId(2, projectId); // 状态2-已完成
        Long inProgressTasks = taskService.countByStatusAndProjectId(1, projectId); // 状态1-进行中
        Long pendingTasks = taskService.countByStatusAndProjectId(0, projectId); // 状态0-待分配

        // 计算逾期任务
        Long overdueTasks = taskService.countOverdueByProjectId(projectId);

        // 计算完成进度
        BigDecimal progress = BigDecimal.ZERO;
        if (totalTasks != null && totalTasks > 0) {
            progress = new BigDecimal(completedTasks != null ? completedTasks : 0)
                    .divide(new BigDecimal(totalTasks), 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"))
                    .setScale(2, RoundingMode.HALF_UP);
        }

        return ProjectProgressDTO.builder()
                .projectId(projectId)
                .projectNo(project.getProjectNo())
                .projectName(project.getProjectName())
                .totalTasks(totalTasks != null ? totalTasks : 0L)
                .completedTasks(completedTasks != null ? completedTasks : 0L)
                .inProgressTasks(inProgressTasks != null ? inProgressTasks : 0L)
                .pendingTasks(pendingTasks != null ? pendingTasks : 0L)
                .overdueTasks(overdueTasks != null ? overdueTasks : 0L)
                .progress(progress)
                .estimatedEndDate(project.getEndDate())
                .build();
    }

    @Override
    public ProjectDTO getByProjectNo(String projectNo) {
        LambdaQueryWrapper<Project> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Project::getProjectNo, projectNo);
        Project project = getOne(wrapper);
        return toDTO(project);
    }

    /**
     * 生成项目编号
     */
    private String generateProjectNo() {
        long timestamp = System.currentTimeMillis();
        String dateStr = LocalDate.now().toString().replace("-", "");
        return "PROJ" + dateStr + String.format("%04d", (int) (timestamp % 10000));
    }

    /**
     * 转换为DTO
     */
    private ProjectDTO toDTO(Project project) {
        if (project == null) {
            return null;
        }

        ProjectDTO dto = new ProjectDTO();
        BeanUtils.copyProperties(project, dto);
        dto.setStatusName(getStatusName(project.getStatus()));
        return dto;
    }

    /**
     * 获取状态名称
     */
    private String getStatusName(Integer status) {
        if (status == null) {
            return "未知";
        }
        return switch (status) {
            case STATUS_DRAFT -> "草稿";
            case STATUS_APPROVED -> "已审批";
            case STATUS_IN_PROGRESS -> "进行中";
            case STATUS_COMPLETED -> "已完成";
            case STATUS_CLOSED -> "已关闭";
            default -> "未知";
        };
    }
}

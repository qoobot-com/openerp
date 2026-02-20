package com.qoobot.qooerp.project.task.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qoobot.qooerp.common.exception.BusinessException;
import com.qoobot.qooerp.common.response.PageResult;
import com.qoobot.qooerp.project.project.dto.ProjectDTO;
import com.qoobot.qooerp.project.project.service.IProjectService;
import com.qoobot.qooerp.project.task.domain.ProjectTask;
import com.qoobot.qooerp.project.task.dto.ProjectTaskDTO;
import com.qoobot.qooerp.project.task.dto.ProjectTaskQueryDTO;
import com.qoobot.qooerp.project.task.mapper.ProjectTaskMapper;
import com.qoobot.qooerp.project.task.service.IProjectTaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 项目任务服务实现类
 *
 * @author QooERP Team
 * @since 2026-02-18
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectTaskServiceImpl extends ServiceImpl<ProjectTaskMapper, ProjectTask>
        implements IProjectTaskService {

    private final IProjectService projectService;

    private static final int STATUS_PENDING = 0;      // 待分配
    private static final int STATUS_IN_PROGRESS = 1;   // 进行中
    private static final int STATUS_COMPLETED = 2;    // 已完成
    private static final int STATUS_CANCELLED = 3;    // 已取消
    private static final int STATUS_BLOCKED = 4;       // 已阻塞

    private static final int PRIORITY_LOW = 1;    // 低
    private static final int PRIORITY_MEDIUM = 2; // 中
    private static final int PRIORITY_HIGH = 3;   // 高
    private static final int PRIORITY_URGENT = 4; // 紧急

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProjectTaskDTO create(ProjectTaskDTO dto) {
        // 验证项目存在
        ProjectDTO project = projectService.getById(dto.getProjectId());
        if (project == null) {
            throw new BusinessException("项目不存在");
        }

        ProjectTask task = new ProjectTask();
        BeanUtils.copyProperties(dto, task);

        // 生成任务编号
        task.setTaskNo(generateTaskNo(dto.getProjectId()));
        task.setStatus(STATUS_PENDING);

        save(task);
        return toDTO(task);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProjectTaskDTO update(Long id, ProjectTaskDTO dto) {
        ProjectTask task = super.getById(id);
        if (task == null) {
            throw new BusinessException("任务不存在");
        }

        // 已完成的任务不能编辑
        if (STATUS_COMPLETED == task.getStatus()) {
            throw new BusinessException("已完成的任务不能编辑");
        }

        BeanUtils.copyProperties(dto, task);
        task.setId(id);
        updateById(task);

        return toDTO(task);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        ProjectTask task = super.getById(id);
        if (task == null) {
            throw new BusinessException("任务不存在");
        }

        // 进行中的任务不能删除
        if (STATUS_IN_PROGRESS == task.getStatus()) {
            throw new BusinessException("进行中的任务不能删除");
        }

        removeById(id);
    }

    @Override
    public ProjectTaskDTO getById(Long id) {
        ProjectTask task = super.getById(id);
        return toDTO(task);
    }

    @Override
    public PageResult<ProjectTaskDTO> page(ProjectTaskQueryDTO queryDTO) {
        LambdaQueryWrapper<ProjectTask> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(queryDTO.getTaskNo())) {
            wrapper.eq(ProjectTask::getTaskNo, queryDTO.getTaskNo());
        }

        if (queryDTO.getProjectId() != null) {
            wrapper.eq(ProjectTask::getProjectId, queryDTO.getProjectId());
        }

        if (StringUtils.hasText(queryDTO.getTaskName())) {
            wrapper.like(ProjectTask::getTaskName, queryDTO.getTaskName());
        }

        if (queryDTO.getStatus() != null) {
            wrapper.eq(ProjectTask::getStatus, queryDTO.getStatus());
        }

        if (queryDTO.getAssigneeId() != null) {
            wrapper.eq(ProjectTask::getAssigneeId, queryDTO.getAssigneeId());
        }

        if (queryDTO.getPriority() != null) {
            wrapper.eq(ProjectTask::getPriority, queryDTO.getPriority());
        }

        if (queryDTO.getStartDateBegin() != null) {
            wrapper.ge(ProjectTask::getStartDate, queryDTO.getStartDateBegin());
        }

        if (queryDTO.getStartDateEnd() != null) {
            wrapper.le(ProjectTask::getStartDate, queryDTO.getStartDateEnd());
        }

        if (queryDTO.getEndDateBegin() != null) {
            wrapper.ge(ProjectTask::getEndDate, queryDTO.getEndDateBegin());
        }

        if (queryDTO.getEndDateEnd() != null) {
            wrapper.le(ProjectTask::getEndDate, queryDTO.getEndDateEnd());
        }

        if (queryDTO.getTenantId() != null) {
            wrapper.eq(ProjectTask::getTenantId, queryDTO.getTenantId());
        }

        wrapper.orderByDesc(ProjectTask::getPriority);
        wrapper.orderByAsc(ProjectTask::getEndDate);
        wrapper.orderByDesc(ProjectTask::getCreateTime);

        Page<ProjectTask> page = new Page<>(queryDTO.getCurrent(), queryDTO.getSize());
        IPage<ProjectTask> taskPage = page(page, wrapper);

        return PageResult.of(
                taskPage.getCurrent(),
                taskPage.getSize(),
                taskPage.getTotal(),
                taskPage.getRecords().stream().map(this::toDTO).toList()
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assign(Long id, Long assigneeId, String assigneeName) {
        ProjectTask task = super.getById(id);
        if (task == null) {
            throw new BusinessException("任务不存在");
        }

        // 已完成的任务不能分配
        if (STATUS_COMPLETED == task.getStatus()) {
            throw new BusinessException("已完成的任务不能重新分配");
        }

        task.setAssigneeId(assigneeId);
        task.setAssigneeName(assigneeName);
        task.setStatus(STATUS_PENDING);
        updateById(task);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void start(Long id) {
        ProjectTask task = super.getById(id);
        if (task == null) {
            throw new BusinessException("任务不存在");
        }

        // 已完成的任务不能开始
        if (STATUS_COMPLETED == task.getStatus()) {
            throw new BusinessException("已完成的任务不能重新开始");
        }

        // 必须先分配负责人
        if (task.getAssigneeId() == null) {
            throw new BusinessException("请先分配负责人");
        }

        task.setStatus(STATUS_IN_PROGRESS);
        updateById(task);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void complete(Long id) {
        ProjectTask task = super.getById(id);
        if (task == null) {
            throw new BusinessException("任务不存在");
        }

        // 已完成的任务
        if (STATUS_COMPLETED == task.getStatus()) {
            return;
        }

        task.setStatus(STATUS_COMPLETED);
        updateById(task);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancel(Long id) {
        ProjectTask task = super.getById(id);
        if (task == null) {
            throw new BusinessException("任务不存在");
        }

        // 已完成的任务不能取消
        if (STATUS_COMPLETED == task.getStatus()) {
            throw new BusinessException("已完成的任务不能取消");
        }

        task.setStatus(STATUS_CANCELLED);
        updateById(task);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void block(Long id) {
        ProjectTask task = super.getById(id);
        if (task == null) {
            throw new BusinessException("任务不存在");
        }

        task.setStatus(STATUS_BLOCKED);
        updateById(task);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unblock(Long id) {
        ProjectTask task = super.getById(id);
        if (task == null) {
            throw new BusinessException("任务不存在");
        }

        if (STATUS_BLOCKED != task.getStatus()) {
            throw new BusinessException("任务状态不是阻塞状态");
        }

        task.setStatus(STATUS_IN_PROGRESS);
        updateById(task);
    }

    @Override
    public Long countByProjectId(Long projectId) {
        return baseMapper.countByProjectId(projectId);
    }

    @Override
    public Long countByStatusAndProjectId(Integer status, Long projectId) {
        return baseMapper.countByStatusAndProjectId(status, projectId);
    }

    @Override
    public Long countOverdueByProjectId(Long projectId) {
        return baseMapper.countOverdueByProjectId(projectId);
    }

    @Override
    public Long countByAssigneeId(Long assigneeId) {
        return baseMapper.countByAssigneeId(assigneeId);
    }

    @Override
    public List<ProjectTaskDTO> listByProjectId(Long projectId) {
        LambdaQueryWrapper<ProjectTask> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProjectTask::getProjectId, projectId);
        wrapper.orderByDesc(ProjectTask::getPriority);
        wrapper.orderByAsc(ProjectTask::getEndDate);
        return list(wrapper).stream().map(this::toDTO).collect(Collectors.toList());
    }

    /**
     * 生成任务编号
     */
    private String generateTaskNo(Long projectId) {
        long timestamp = System.currentTimeMillis();
        return "TASK" + projectId + String.format("%08d", (int) (timestamp % 100000000));
    }

    /**
     * 转换为DTO
     */
    private ProjectTaskDTO toDTO(ProjectTask task) {
        if (task == null) {
            return null;
        }

        ProjectTaskDTO dto = new ProjectTaskDTO();
        BeanUtils.copyProperties(task, dto);
        dto.setStatusName(getStatusName(task.getStatus()));
        dto.setPriorityName(getPriorityName(task.getPriority()));

        // 查询项目名称
        if (task.getProjectId() != null) {
            ProjectDTO project = projectService.getById(task.getProjectId());
            if (project != null) {
                dto.setProjectName(project.getProjectName());
            }
        }

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
            case STATUS_PENDING -> "待分配";
            case STATUS_IN_PROGRESS -> "进行中";
            case STATUS_COMPLETED -> "已完成";
            case STATUS_CANCELLED -> "已取消";
            case STATUS_BLOCKED -> "已阻塞";
            default -> "未知";
        };
    }

    /**
     * 获取优先级名称
     */
    private String getPriorityName(Integer priority) {
        if (priority == null) {
            return "未知";
        }
        return switch (priority) {
            case PRIORITY_LOW -> "低";
            case PRIORITY_MEDIUM -> "中";
            case PRIORITY_HIGH -> "高";
            case PRIORITY_URGENT -> "紧急";
            default -> "未知";
        };
    }
}

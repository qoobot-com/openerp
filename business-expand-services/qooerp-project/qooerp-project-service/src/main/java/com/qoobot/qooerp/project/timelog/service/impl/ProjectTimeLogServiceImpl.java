package com.qoobot.qooerp.project.timelog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qoobot.qooerp.common.exception.BusinessException;
import com.qoobot.qooerp.common.response.PageResult;
import com.qoobot.qooerp.project.timelog.domain.ProjectTimeLog;
import com.qoobot.qooerp.project.timelog.dto.ProjectTimeLogDTO;
import com.qoobot.qooerp.project.timelog.mapper.ProjectTimeLogMapper;
import com.qoobot.qooerp.project.timelog.service.IProjectTimeLogService;
import com.qoobot.qooerp.project.task.dto.ProjectTaskDTO;
import com.qoobot.qooerp.project.task.service.IProjectTaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 工时记录服务实现类
 *
 * @author QooERP Team
 * @since 2026-02-18
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectTimeLogServiceImpl extends ServiceImpl<ProjectTimeLogMapper, ProjectTimeLog>
        implements IProjectTimeLogService {

    private final IProjectTaskService taskService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProjectTimeLogDTO create(ProjectTimeLogDTO dto) {
        if (dto.getTaskId() == null) {
            throw new BusinessException("任务ID不能为空");
        }

        ProjectTaskDTO task = taskService.getById(dto.getTaskId());
        if (task == null) {
            throw new BusinessException("任务不存在");
        }

        ProjectTimeLog timeLog = new ProjectTimeLog();
        BeanUtils.copyProperties(dto, timeLog);

        // 计算工作时长
        if (dto.getStartTime() != null && dto.getEndTime() != null) {
            Duration duration = Duration.between(dto.getStartTime(), dto.getEndTime());
            long minutes = duration.toMinutes();
            timeLog.setHours(new BigDecimal(minutes)
                    .divide(new BigDecimal(60), 2, RoundingMode.HALF_UP));
        }

        save(timeLog);
        return toDTO(timeLog);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProjectTimeLogDTO update(Long id, ProjectTimeLogDTO dto) {
        ProjectTimeLog timeLog = super.getById(id);
        if (timeLog == null) {
            throw new BusinessException("工时记录不存在");
        }

        BeanUtils.copyProperties(dto, timeLog);

        // 重新计算工作时长
        if (dto.getStartTime() != null && dto.getEndTime() != null) {
            Duration duration = Duration.between(dto.getStartTime(), dto.getEndTime());
            long minutes = duration.toMinutes();
            timeLog.setHours(new BigDecimal(minutes)
                    .divide(new BigDecimal(60), 2, RoundingMode.HALF_UP));
        }

        timeLog.setId(id);
        updateById(timeLog);

        return toDTO(timeLog);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        ProjectTimeLog timeLog = super.getById(id);
        if (timeLog == null) {
            throw new BusinessException("工时记录不存在");
        }

        removeById(id);
    }

    @Override
    public ProjectTimeLogDTO getById(Long id) {
        ProjectTimeLog timeLog = super.getById(id);
        return toDTO(timeLog);
    }

    @Override
    public PageResult<ProjectTimeLogDTO> page(Long taskId, Long userId, Long current, Long size, Long tenantId) {
        LambdaQueryWrapper<ProjectTimeLog> wrapper = new LambdaQueryWrapper<>();

        if (taskId != null) {
            wrapper.eq(ProjectTimeLog::getTaskId, taskId);
        }

        if (userId != null) {
            wrapper.eq(ProjectTimeLog::getUserId, userId);
        }

        wrapper.eq(ProjectTimeLog::getTenantId, tenantId);
        wrapper.orderByDesc(ProjectTimeLog::getStartTime);

        Page<ProjectTimeLog> page = new Page<>(current, size);
        IPage<ProjectTimeLog> timeLogPage = page(page, wrapper);

        return PageResult.of(
                timeLogPage.getCurrent(),
                timeLogPage.getSize(),
                timeLogPage.getTotal(),
                timeLogPage.getRecords().stream().map(this::toDTO).toList()
        );
    }

    @Override
    public List<ProjectTimeLogDTO> listByTaskId(Long taskId) {
        LambdaQueryWrapper<ProjectTimeLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProjectTimeLog::getTaskId, taskId);
        wrapper.orderByDesc(ProjectTimeLog::getStartTime);

        return list(wrapper).stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<ProjectTimeLogDTO> listByUserId(Long userId) {
        LambdaQueryWrapper<ProjectTimeLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProjectTimeLog::getUserId, userId);
        wrapper.orderByDesc(ProjectTimeLog::getStartTime);

        return list(wrapper).stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public BigDecimal sumHoursByTaskId(Long taskId) {
        LambdaQueryWrapper<ProjectTimeLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProjectTimeLog::getTaskId, taskId);
        wrapper.select(ProjectTimeLog::getHours);

        List<ProjectTimeLog> list = list(wrapper);
        return list.stream()
                .map(ProjectTimeLog::getHours)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public BigDecimal sumHoursByUserId(Long userId) {
        LambdaQueryWrapper<ProjectTimeLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProjectTimeLog::getUserId, userId);
        wrapper.select(ProjectTimeLog::getHours);

        List<ProjectTimeLog> list = list(wrapper);
        return list.stream()
                .map(ProjectTimeLog::getHours)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private ProjectTimeLogDTO toDTO(ProjectTimeLog timeLog) {
        if (timeLog == null) {
            return null;
        }

        ProjectTimeLogDTO dto = new ProjectTimeLogDTO();
        BeanUtils.copyProperties(timeLog, dto);

        if (timeLog.getTaskId() != null) {
            ProjectTaskDTO task = taskService.getById(timeLog.getTaskId());
            if (task != null) {
                dto.setTaskName(task.getTaskName());
                // TODO: 获取项目名称
            }
        }

        return dto;
    }
}

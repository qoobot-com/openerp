package com.qoobot.qooerp.project.attachment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qoobot.qooerp.common.exception.BusinessException;
import com.qoobot.qooerp.common.response.PageResult;
import com.qoobot.qooerp.project.attachment.domain.ProjectAttachment;
import com.qoobot.qooerp.project.attachment.dto.ProjectAttachmentDTO;
import com.qoobot.qooerp.project.attachment.mapper.ProjectAttachmentMapper;
import com.qoobot.qooerp.project.attachment.service.IProjectAttachmentService;
import com.qoobot.qooerp.project.project.dto.ProjectDTO;
import com.qoobot.qooerp.project.project.service.IProjectService;
import com.qoobot.qooerp.project.task.dto.ProjectTaskDTO;
import com.qoobot.qooerp.project.task.service.IProjectTaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 项目附件服务实现类
 *
 * @author QooERP Team
 * @since 2026-02-18
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectAttachmentServiceImpl extends ServiceImpl<ProjectAttachmentMapper, ProjectAttachment>
        implements IProjectAttachmentService {

    private final IProjectService projectService;
    private final IProjectTaskService taskService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProjectAttachmentDTO upload(ProjectAttachmentDTO dto) {
        if (dto.getProjectId() == null) {
            throw new BusinessException("项目ID不能为空");
        }

        ProjectDTO project = projectService.getById(dto.getProjectId());
        if (project == null) {
            throw new BusinessException("项目不存在");
        }

        // 如果关联任务，验证任务存在
        if (dto.getTaskId() != null) {
            ProjectTaskDTO task = taskService.getById(dto.getTaskId());
            if (task == null) {
                throw new BusinessException("任务不存在");
            }
        }

        ProjectAttachment attachment = new ProjectAttachment();
        BeanUtils.copyProperties(dto, attachment);

        save(attachment);
        return toDTO(attachment);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        ProjectAttachment attachment = super.getById(id);
        if (attachment == null) {
            throw new BusinessException("附件不存在");
        }

        // TODO: 删除实际文件
        // File file = new File(attachment.getFilePath());
        // if (file.exists()) {
        //     file.delete();
        // }

        removeById(id);
    }

    @Override
    public ProjectAttachmentDTO getById(Long id) {
        ProjectAttachment attachment = super.getById(id);
        return toDTO(attachment);
    }

    @Override
    public PageResult<ProjectAttachmentDTO> page(Long projectId, Long taskId, Long current, Long size, Long tenantId) {
        LambdaQueryWrapper<ProjectAttachment> wrapper = new LambdaQueryWrapper<>();

        if (projectId != null) {
            wrapper.eq(ProjectAttachment::getProjectId, projectId);
        }

        if (taskId != null) {
            wrapper.eq(ProjectAttachment::getTaskId, taskId);
        }

        wrapper.eq(ProjectAttachment::getTenantId, tenantId);
        wrapper.orderByDesc(ProjectAttachment::getCreateTime);

        Page<ProjectAttachment> page = new Page<>(current, size);
        IPage<ProjectAttachment> attachmentPage = page(page, wrapper);

        return PageResult.of(
                attachmentPage.getCurrent(),
                attachmentPage.getSize(),
                attachmentPage.getTotal(),
                attachmentPage.getRecords().stream().map(this::toDTO).toList()
        );
    }

    @Override
    public List<ProjectAttachmentDTO> listByProjectId(Long projectId) {
        LambdaQueryWrapper<ProjectAttachment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProjectAttachment::getProjectId, projectId);
        wrapper.orderByDesc(ProjectAttachment::getCreateTime);

        return list(wrapper).stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<ProjectAttachmentDTO> listByTaskId(Long taskId) {
        LambdaQueryWrapper<ProjectAttachment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProjectAttachment::getTaskId, taskId);
        wrapper.orderByDesc(ProjectAttachment::getCreateTime);

        return list(wrapper).stream().map(this::toDTO).collect(Collectors.toList());
    }

    private ProjectAttachmentDTO toDTO(ProjectAttachment attachment) {
        if (attachment == null) {
            return null;
        }

        ProjectAttachmentDTO dto = new ProjectAttachmentDTO();
        BeanUtils.copyProperties(attachment, dto);
        dto.setFileSizeDisplay(formatFileSize(attachment.getFileSize()));

        if (attachment.getProjectId() != null) {
            ProjectDTO project = projectService.getById(attachment.getProjectId());
            if (project != null) {
                dto.setProjectName(project.getProjectName());
            }
        }

        if (attachment.getTaskId() != null) {
            ProjectTaskDTO task = taskService.getById(attachment.getTaskId());
            if (task != null) {
                dto.setTaskName(task.getTaskName());
            }
        }

        return dto;
    }

    private String formatFileSize(Long size) {
        if (size == null) {
            return "0 B";
        }

        if (size < 1024) {
            return size + " B";
        } else if (size < 1024 * 1024) {
            return String.format("%.2f KB", size / 1024.0);
        } else if (size < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", size / (1024.0 * 1024));
        } else {
            return String.format("%.2f GB", size / (1024.0 * 1024 * 1024));
        }
    }
}

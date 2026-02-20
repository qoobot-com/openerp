package com.qoobot.qooerp.project.comment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qoobot.qooerp.common.exception.BusinessException;
import com.qoobot.qooerp.common.response.PageResult;
import com.qoobot.qooerp.project.comment.domain.ProjectComment;
import com.qoobot.qooerp.project.comment.dto.ProjectCommentDTO;
import com.qoobot.qooerp.project.comment.mapper.ProjectCommentMapper;
import com.qoobot.qooerp.project.comment.service.IProjectCommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 项目评论服务实现类
 *
 * @author QooERP Team
 * @since 2026-02-18
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectCommentServiceImpl extends ServiceImpl<ProjectCommentMapper, ProjectComment>
        implements IProjectCommentService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProjectCommentDTO create(ProjectCommentDTO dto) {
        if (!StringUtils.hasText(dto.getContent())) {
            throw new BusinessException("评论内容不能为空");
        }

        ProjectComment comment = new ProjectComment();
        BeanUtils.copyProperties(dto, comment);

        // 处理@提及的用户
        if (dto.getMentionedUsers() != null && !dto.getMentionedUsers().isEmpty()) {
            comment.setMentionedUsers(String.join(",", dto.getMentionedUsers().stream()
                    .map(String::valueOf).collect(Collectors.toList())));
        }

        save(comment);
        return toDTO(comment);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProjectCommentDTO update(Long id, ProjectCommentDTO dto) {
        ProjectComment comment = super.getById(id);
        if (comment == null) {
            throw new BusinessException("评论不存在");
        }

        if (!StringUtils.hasText(dto.getContent())) {
            throw new BusinessException("评论内容不能为空");
        }

        BeanUtils.copyProperties(dto, comment);

        if (dto.getMentionedUsers() != null) {
            comment.setMentionedUsers(String.join(",", dto.getMentionedUsers().stream()
                    .map(String::valueOf).collect(Collectors.toList())));
        }

        comment.setId(id);
        updateById(comment);

        return toDTO(comment);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        ProjectComment comment = super.getById(id);
        if (comment == null) {
            throw new BusinessException("评论不存在");
        }

        removeById(id);
    }

    @Override
    public ProjectCommentDTO getById(Long id) {
        ProjectComment comment = super.getById(id);
        return toDTO(comment);
    }

    @Override
    public PageResult<ProjectCommentDTO> page(String entityType, Long entityId, Long current, Long size, Long tenantId) {
        LambdaQueryWrapper<ProjectComment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProjectComment::getEntityType, entityType);
        wrapper.eq(ProjectComment::getEntityId, entityId);
        wrapper.eq(ProjectComment::getTenantId, tenantId);
        wrapper.orderByDesc(ProjectComment::getCreateTime);

        Page<ProjectComment> page = new Page<>(current, size);
        IPage<ProjectComment> commentPage = page(page, wrapper);

        return PageResult.of(
                commentPage.getCurrent(),
                commentPage.getSize(),
                commentPage.getTotal(),
                commentPage.getRecords().stream().map(this::toDTO).toList()
        );
    }

    @Override
    public List<ProjectCommentDTO> listByEntity(String entityType, Long entityId) {
        LambdaQueryWrapper<ProjectComment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProjectComment::getEntityType, entityType);
        wrapper.eq(ProjectComment::getEntityId, entityId);
        wrapper.orderByDesc(ProjectComment::getCreateTime);

        return list(wrapper).stream().map(this::toDTO).collect(Collectors.toList());
    }

    private ProjectCommentDTO toDTO(ProjectComment comment) {
        if (comment == null) {
            return null;
        }

        ProjectCommentDTO dto = new ProjectCommentDTO();
        BeanUtils.copyProperties(comment, dto);

        // 解析@提及的用户
        if (StringUtils.hasText(comment.getMentionedUsers())) {
            dto.setMentionedUsers(Arrays.stream(comment.getMentionedUsers().split(","))
                    .map(Long::valueOf)
                    .collect(Collectors.toList()));
        }

        return dto;
    }
}

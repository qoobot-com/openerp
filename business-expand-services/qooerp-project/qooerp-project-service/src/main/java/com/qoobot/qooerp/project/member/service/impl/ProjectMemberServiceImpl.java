package com.qoobot.qooerp.project.member.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qoobot.qooerp.common.exception.BusinessException;
import com.qoobot.qooerp.project.member.domain.ProjectMember;
import com.qoobot.qooerp.project.member.dto.ProjectMemberDTO;
import com.qoobot.qooerp.project.member.mapper.ProjectMemberMapper;
import com.qoobot.qooerp.project.member.service.IProjectMemberService;
import com.qoobot.qooerp.project.project.dto.ProjectDTO;
import com.qoobot.qooerp.project.project.service.IProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 项目成员服务实现类
 *
 * @author QooERP Team
 * @since 2026-02-18
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectMemberServiceImpl extends ServiceImpl<ProjectMemberMapper, ProjectMember>
        implements IProjectMemberService {

    private final IProjectService projectService;

    private static final int ROLE_MANAGER = 1;     // 项目经理
    private static final int ROLE_DEVELOPER = 2;   // 开发人员
    private static final int ROLE_TESTER = 3;      // 测试人员
    private static final int ROLE_OBSERVER = 4;      // 观察者

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addMember(Long projectId, Long userId, String userName, Integer role) {
        ProjectDTO project = projectService.getById(projectId);
        if (project == null) {
            throw new BusinessException("项目不存在");
        }

        // 检查是否已存在
        LambdaQueryWrapper<ProjectMember> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProjectMember::getProjectId, projectId);
        wrapper.eq(ProjectMember::getUserId, userId);
        ProjectMember existMember = getOne(wrapper);

        if (existMember != null) {
            throw new BusinessException("该用户已在项目中");
        }

        ProjectMember member = new ProjectMember();
        member.setProjectId(projectId);
        member.setUserId(userId);
        member.setUserName(userName);
        member.setRole(role);

        save(member);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeMember(Long projectId, Long userId) {
        ProjectDTO project = projectService.getById(projectId);
        if (project == null) {
            throw new BusinessException("项目不存在");
        }

        // 查询成员信息
        LambdaQueryWrapper<ProjectMember> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProjectMember::getProjectId, projectId);
        wrapper.eq(ProjectMember::getUserId, userId);
        ProjectMember member = getOne(wrapper);

        if (member == null) {
            throw new BusinessException("成员不存在");
        }

        // 不能移除项目经理
        if (ROLE_MANAGER == member.getRole()) {
            throw new BusinessException("不能移除项目经理");
        }

        removeById(member.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateMemberRole(Long projectId, Long userId, Integer role) {
        LambdaQueryWrapper<ProjectMember> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProjectMember::getProjectId, projectId);
        wrapper.eq(ProjectMember::getUserId, userId);
        ProjectMember member = getOne(wrapper);

        if (member == null) {
            throw new BusinessException("成员不存在");
        }

        // 不能将项目经理降级
        if (ROLE_MANAGER == member.getRole() && ROLE_MANAGER != role) {
            throw new BusinessException("不能将项目经理降级");
        }

        member.setRole(role);
        updateById(member);
    }

    @Override
    public List<ProjectMemberDTO> listByProjectId(Long projectId) {
        LambdaQueryWrapper<ProjectMember> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProjectMember::getProjectId, projectId);
        wrapper.orderByAsc(ProjectMember::getRole);

        return list(wrapper).stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<ProjectMemberDTO> listByUserId(Long userId) {
        LambdaQueryWrapper<ProjectMember> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProjectMember::getUserId, userId);
        wrapper.orderByDesc(ProjectMember::getCreateTime);

        return list(wrapper).stream().map(this::toDTO).collect(Collectors.toList());
    }

    private ProjectMemberDTO toDTO(ProjectMember member) {
        if (member == null) {
            return null;
        }

        ProjectMemberDTO dto = new ProjectMemberDTO();
        BeanUtils.copyProperties(member, dto);
        dto.setRoleName(getRoleName(member.getRole()));

        if (member.getProjectId() != null) {
            ProjectDTO project = projectService.getById(member.getProjectId());
            if (project != null) {
                dto.setProjectName(project.getProjectName());
            }
        }

        return dto;
    }

    private String getRoleName(Integer role) {
        if (role == null) {
            return "未知";
        }
        return switch (role) {
            case ROLE_MANAGER -> "项目经理";
            case ROLE_DEVELOPER -> "开发人员";
            case ROLE_TESTER -> "测试人员";
            case ROLE_OBSERVER -> "观察者";
            default -> "未知";
        };
    }
}

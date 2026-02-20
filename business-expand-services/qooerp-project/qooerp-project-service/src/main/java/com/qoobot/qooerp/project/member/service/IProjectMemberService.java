package com.qoobot.qooerp.project.member.service;

import com.qoobot.qooerp.project.member.dto.ProjectMemberDTO;

import java.util.List;

/**
 * 项目成员服务接口
 *
 * @author QooERP Team
 * @since 2026-02-18
 */
public interface IProjectMemberService {

    /**
     * 添加成员
     */
    void addMember(Long projectId, Long userId, String userName, Integer role);

    /**
     * 移除成员
     */
    void removeMember(Long projectId, Long userId);

    /**
     * 更新成员角色
     */
    void updateMemberRole(Long projectId, Long userId, Integer role);

    /**
     * 根据项目ID查询成员列表
     */
    List<ProjectMemberDTO> listByProjectId(Long projectId);

    /**
     * 根据用户ID查询参与的项目
     */
    List<ProjectMemberDTO> listByUserId(Long userId);
}

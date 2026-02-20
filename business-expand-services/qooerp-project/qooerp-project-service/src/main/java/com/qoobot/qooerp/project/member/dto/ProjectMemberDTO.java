package com.qoobot.qooerp.project.member.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 项目成员DTO
 *
 * @author QooERP Team
 * @since 2026-02-18
 */
@Data
public class ProjectMemberDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 成员ID
     */
    private Long id;

    /**
     * 项目ID
     */
    private Long projectId;

    /**
     * 项目名称
     */
    private String projectName;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户姓名
     */
    private String userName;

    /**
     * 角色(1-项目经理,2-开发人员,3-测试人员,4-观察者)
     */
    private Integer role;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}

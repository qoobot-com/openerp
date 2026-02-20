package com.qoobot.qooerp.project.member.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 项目成员实体类
 *
 * @author QooERP Team
 * @since 2026-02-18
 */
@Data
@TableName("project_member")
public class ProjectMember implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 项目ID
     */
    private Long projectId;

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
     * 租户ID
     */
    private Long tenantId;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 是否删除(0-否,1-是)
     */
    @TableLogic
    private Integer deleted;
}

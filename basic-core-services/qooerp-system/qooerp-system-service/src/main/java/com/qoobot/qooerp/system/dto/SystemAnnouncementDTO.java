package com.qoobot.qooerp.system.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 系统公告DTO
 */
@Data
public class SystemAnnouncementDTO {

    /**
     * 公告ID
     */
    private Long id;

    /**
     * 公告标题
     */
    @NotBlank(message = "公告标题不能为空")
    private String title;

    /**
     * 公告内容
     */
    @NotBlank(message = "公告内容不能为空")
    private String content;

    /**
     * 公告类型：urgent-紧急，important-重要，normal-普通
     */
    @NotBlank(message = "公告类型不能为空")
    private String announcementType;

    /**
     * 目标类型：all-全部用户，department-指定部门，role-指定角色
     */
    @NotBlank(message = "目标类型不能为空")
    private String targetType;

    /**
     * 目标ID列表（JSON格式）
     */
    private String targetIds;

    /**
     * 优先级
     */
    @NotNull(message = "优先级不能为空")
    private Integer priority;

    /**
     * 状态：0-草稿，1-已发布，2-已撤回
     */
    private Integer status;
}

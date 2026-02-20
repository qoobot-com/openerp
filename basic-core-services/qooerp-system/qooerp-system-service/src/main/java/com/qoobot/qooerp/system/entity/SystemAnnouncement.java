package com.qoobot.qooerp.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 系统公告实体类
 */
@Data
@TableName("system_announcement")
public class SystemAnnouncement {

    /**
     * 公告ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 公告标题
     */
    private String title;

    /**
     * 公告内容
     */
    private String content;

    /**
     * 公告类型：urgent-紧急，important-重要，normal-普通
     */
    private String announcementType;

    /**
     * 目标类型：all-全部用户，department-指定部门，role-指定角色
     */
    private String targetType;

    /**
     * 目标ID列表（JSON格式）
     */
    private String targetIds;

    /**
     * 发布人ID
     */
    private Long publisherId;

    /**
     * 发布人姓名
     */
    private String publisherName;

    /**
     * 发布时间
     */
    private LocalDateTime publishTime;

    /**
     * 状态：0-草稿，1-已发布，2-已撤回
     */
    private Integer status;

    /**
     * 优先级
     */
    private Integer priority;

    /**
     * 阅读次数
     */
    private Integer readCount;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 创建人
     */
    @TableField(fill = FieldFill.INSERT)
    private String createBy;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 更新人
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updateBy;
}

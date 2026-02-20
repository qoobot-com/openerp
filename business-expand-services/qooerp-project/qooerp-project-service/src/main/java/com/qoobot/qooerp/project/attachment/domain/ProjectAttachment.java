package com.qoobot.qooerp.project.attachment.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 项目附件实体类
 *
 * @author QooERP Team
 * @since 2026-02-18
 */
@Data
@TableName("project_attachment")
public class ProjectAttachment implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 项目ID
     */
    private Long projectId;

    /**
     * 任务ID
     */
    private Long taskId;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 文件路径
     */
    private String filePath;

    /**
     * 文件大小(字节)
     */
    private Long fileSize;

    /**
     * 文件类型(扩展名)
     */
    private String fileType;

    /**
     * 上传用户ID
     */
    private Long uploadUserId;

    /**
     * 上传用户姓名
     */
    private String uploadUserName;

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

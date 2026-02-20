package com.qoobot.qooerp.project.attachment.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 项目附件DTO
 *
 * @author QooERP Team
 * @since 2026-02-18
 */
@Data
public class ProjectAttachmentDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 附件ID
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
     * 任务ID
     */
    private Long taskId;

    /**
     * 任务名称
     */
    private String taskName;

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
     * 文件大小(格式化)
     */
    private String fileSizeDisplay;

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
    private LocalDateTime createTime;
}

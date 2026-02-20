package com.qoobot.qooerp.file.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文件信息DTO
 *
 * @author QooERP
 * @date 2026-02-17
 */
@Data
public class FileInfoDTO {

    /**
     * 主键
     */
    private Long id;

    /**
     * 文件编号
     */
    private String fileNo;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 文件路径
     */
    private String filePath;

    /**
     * 文件大小（字节）
     */
    private Long fileSize;

    /**
     * 文件类型（MIME）
     */
    private String fileType;

    /**
     * 文件扩展名
     */
    private String fileExtension;

    /**
     * 存储类型
     */
    private String storageType;

    /**
     * 所属文件夹ID
     */
    private Long folderId;

    /**
     * 文件夹名称
     */
    private String folderName;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 下载次数
     */
    private Integer downloadCount;

    /**
     * 文件URL
     */
    private String fileUrl;

    /**
     * 缩略图URL
     */
    private String thumbnailUrl;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}

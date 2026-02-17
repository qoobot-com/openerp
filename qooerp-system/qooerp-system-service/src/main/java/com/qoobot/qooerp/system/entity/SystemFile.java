package com.qoobot.qooerp.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 文件表实体
 *
 * @author QooERP Team
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("system_file")
public class SystemFile {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 文件名
     */
    @TableField("file_name")
    private String fileName;

    /**
     * 原始文件名
     */
    @TableField("original_name")
    private String originalName;

    /**
     * 文件扩展名
     */
    @TableField("file_ext")
    private String fileExt;

    /**
     * 文件大小(字节)
     */
    @TableField("file_size")
    private Long fileSize;

    /**
     * MIME类型
     */
    @TableField("mime_type")
    private String mimeType;

    /**
     * 存储类型: local/oss
     */
    @TableField("storage_type")
    private String storageType;

    /**
     * 文件路径
     */
    @TableField("file_path")
    private String filePath;

    /**
     * 文件访问URL
     */
    @TableField("file_url")
    private String fileUrl;

    /**
     * 文件MD5
     */
    @TableField("md5")
    private String md5;

    /**
     * 上传人
     */
    @TableField("uploader")
    private String uploader;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}

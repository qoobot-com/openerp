package com.qoobot.qooerp.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文件VO
 *
 * @author QooERP Team
 * @version 1.0.0
 */
@Data
@Schema(description = "文件VO")
public class SystemFileVO {

    @Schema(description = "文件ID")
    private Long id;

    @Schema(description = "文件名")
    private String fileName;

    @Schema(description = "原始文件名")
    private String originalName;

    @Schema(description = "文件扩展名")
    private String fileExt;

    @Schema(description = "文件大小(字节)")
    private Long fileSize;

    @Schema(description = "MIME类型")
    private String mimeType;

    @Schema(description = "存储类型")
    private String storageType;

    @Schema(description = "文件路径")
    private String filePath;

    @Schema(description = "文件访问URL")
    private String fileUrl;

    @Schema(description = "文件MD5")
    private String md5;

    @Schema(description = "上传人")
    private String uploader;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}

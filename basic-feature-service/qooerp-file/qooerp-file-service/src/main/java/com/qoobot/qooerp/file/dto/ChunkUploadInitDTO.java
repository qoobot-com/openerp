package com.qoobot.qooerp.file.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * 分片上传初始化DTO
 *
 * @author QooERP
 * @date 20xx-xx-xx
 */
@Data
public class ChunkUploadInitDTO {

    /**
     * 文件名称
     */
    @NotBlank(message = "文件名称不能为空")
    private String fileName;

    /**
     * 文件大小
     */
    @NotNull(message = "文件大小不能为空")
    private Long fileSize;

    /**
     * 文件MD5
     */
    @NotBlank(message = "文件MD5不能为空")
    private String fileMd5;

    /**
     * 分片大小
     */
    @NotNull(message = "分片大小不能为空")
    @Positive(message = "分片大小必须大于0")
    private Integer chunkSize;

    /**
     * 总分片数
     */
    @NotNull(message = "总分片数不能为空")
    private Integer totalChunks;

    /**
     * 文件夹ID
     */
    private Long folderId;
}

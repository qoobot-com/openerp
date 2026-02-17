package com.qoobot.qooerp.file.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;

/**
 * 分片上传完成DTO
 *
 * @author QooERP
 * @date 2026-02-17
 */
@Data
public class ChunkUploadCompleteDTO {

    /**
     * 任务ID
     */
    @NotBlank(message = "任务ID不能为空")
    private String taskId;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 文件夹ID
     */
    private Long folderId;
}

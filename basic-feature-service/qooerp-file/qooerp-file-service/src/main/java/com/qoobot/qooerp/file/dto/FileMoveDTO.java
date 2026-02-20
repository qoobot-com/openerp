package com.qoobot.qooerp.file.dto;

import lombok.Data;

import jakarta.validation.constraints.NotNull;

/**
 * 文件移动DTO
 *
 * @author QooERP
 * @date 2026-02-17
 */
@Data
public class FileMoveDTO {

    /**
     * 目标文件夹ID
     */
    @NotNull(message = "目标文件夹ID不能为空")
    private Long targetFolderId;
}

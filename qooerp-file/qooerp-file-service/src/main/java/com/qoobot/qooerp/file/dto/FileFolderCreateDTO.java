package com.qoobot.qooerp.file.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 文件夹创建DTO
 *
 * @author QooERP
 * @date 2026-02-17
 */
@Data
public class FileFolderCreateDTO {

    /**
     * 文件夹名称
     */
    @NotBlank(message = "文件夹名称不能为空")
    private String folderName;

    /**
     * 父文件夹ID（0表示根目录）
     */
    @NotNull(message = "父文件夹ID不能为空")
    private Long parentId;
}

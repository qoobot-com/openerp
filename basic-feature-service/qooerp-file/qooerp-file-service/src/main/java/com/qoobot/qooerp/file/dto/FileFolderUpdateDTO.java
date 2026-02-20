package com.qoobot.qooerp.file.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;

/**
 * 文件夹更新DTO
 *
 * @author QooERP
 * @date 20xx-xx-xx
 */
@Data
public class FileFolderUpdateDTO {

    /**
     * 文件夹名称
     */
    @NotBlank(message = "文件夹名称不能为空")
    private String folderName;

    /**
     * 排序
     */
    private Integer sort;
}

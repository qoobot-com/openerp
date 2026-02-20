package com.qoobot.qooerp.file.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;

/**
 * 文件重命名DTO
 *
 * @author QooERP
 * @date 20xx-xx-xx
 */
@Data
public class FileRenameDTO {

    /**
     * 新文件名
     */
    @NotBlank(message = "文件名不能为空")
    private String newFileName;
}

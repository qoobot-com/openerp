package com.qoobot.qooerp.file.dto;

import lombok.Data;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

/**
 * 文件分享创建DTO
 *
 * @author QooERP
 * @date 20xx-xx-xx
 */
@Data
public class FileShareCreateDTO {

    /**
     * 分享密码
     */
    private String sharePassword;

    /**
     * 分享标题
     */
    private String shareName;

    /**
     * 分享描述
     */
    private String shareDesc;

    /**
     * 有效期（天数）
     */
    @Min(value = 1, message = "有效期最少1天")
    @Max(value = 365, message = "有效期最多365天")
    private Integer expireDays;
}

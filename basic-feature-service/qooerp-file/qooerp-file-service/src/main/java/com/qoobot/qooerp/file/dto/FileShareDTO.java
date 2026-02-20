package com.qoobot.qooerp.file.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文件分享DTO
 *
 * @author QooERP
 * @date 20xx-xx-xx
 */
@Data
public class FileShareDTO {

    /**
     * 主键
     */
    private Long id;

    /**
     * 文件ID
     */
    private Long fileId;

    /**
     * 分享码
     */
    private String shareCode;

    /**
     * 分享标题
     */
    private String shareName;

    /**
     * 分享描述
     */
    private String shareDesc;

    /**
     * 是否有密码
     */
    private Boolean hasPassword;

    /**
     * 过期时间
     */
    private LocalDateTime expireTime;

    /**
     * 访问次数
     */
    private Integer visitCount;

    /**
     * 下载次数
     */
    private Integer downloadCount;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 分享链接
     */
    private String shareUrl;

    /**
     * 文件信息
     */
    private FileInfoDTO fileInfo;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}

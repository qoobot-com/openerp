package com.qoobot.qooerp.file.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 文件夹DTO
 *
 * @author QooERP
 * @date 20xx-xx-xx
 */
@Data
public class FileFolderDTO {

    /**
     * 主键
     */
    private Long id;

    /**
     * 文件夹编号
     */
    private String folderNo;

    /**
     * 文件夹名称
     */
    private String folderName;

    /**
     * 父文件夹ID
     */
    private Long parentId;

    /**
     * 文件夹路径
     */
    private String folderPath;

    /**
     * 层级
     */
    private Integer folderLevel;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 子文件夹列表
     */
    private List<FileFolderDTO> children;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}

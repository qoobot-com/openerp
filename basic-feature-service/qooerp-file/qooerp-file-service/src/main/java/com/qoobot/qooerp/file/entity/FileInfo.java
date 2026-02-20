package com.qoobot.qooerp.file.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qoobot.qooerp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 文件信息实体
 *
 * @author QooERP
 * @date 2026-02-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("file_info")
public class FileInfo extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 文件编号
     */
    private String fileNo;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 文件路径
     */
    private String filePath;

    /**
     * 文件大小（字节）
     */
    private Long fileSize;

    /**
     * 文件类型（MIME）
     */
    private String fileType;

    /**
     * 文件扩展名
     */
    private String fileExtension;

    /**
     * 文件MD5
     */
    private String fileMd5;

    /**
     * 存储类型（minio/oss/local）
     */
    private String storageType;

    /**
     * 所属文件夹ID
     */
    private Long folderId;

    /**
     * 状态（1-正常 2-删除中 3-已删除）
     */
    private Integer status;

    /**
     * 下载次数
     */
    private Integer downloadCount;

    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * 创建人ID
     */
    private Long creatorId;
}

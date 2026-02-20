package com.qoobot.qooerp.file.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qoobot.qooerp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 文件夹实体
 *
 * @author QooERP
 * @date 2026-02-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("file_folder")
public class FileFolder extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
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
     * 父文件夹ID（0表示根目录）
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
     * 状态（1-正常 2-已删除）
     */
    private Integer status;

    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * 创建人ID
     */
    private Long creatorId;
}

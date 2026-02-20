package com.qoobot.qooerp.file.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 存储配额实体
 *
 * @author QooERP
 * @date 20xx-xx-xx
 */
@Data
@TableName("file_quota")
public class FileQuota {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * 总配额（默认10GB）
     */
    private Long totalQuota;

    /**
     * 已使用（字节）
     */
    private Long usedQuota;

    /**
     * 文件数量
     */
    private Integer fileCount;

    /**
     * 文件夹数量
     */
    private Integer folderCount;

    /**
     * 最后计算时间
     */
    private LocalDateTime lastCalculateTime;
}

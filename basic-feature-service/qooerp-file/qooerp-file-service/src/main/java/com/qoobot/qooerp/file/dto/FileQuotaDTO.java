package com.qoobot.qooerp.file.dto;

import lombok.Data;

/**
 * 存储配额DTO
 *
 * @author QooERP
 * @date 20xx-xx-xx
 */
@Data
public class FileQuotaDTO {

    /**
     * 总配额（字节）
     */
    private Long totalQuota;

    /**
     * 已使用（字节）
     */
    private Long usedQuota;

    /**
     * 剩余配额（字节）
     */
    private Long remainingQuota;

    /**
     * 使用率（百分比）
     */
    private Double usageRate;

    /**
     * 文件数量
     */
    private Integer fileCount;

    /**
     * 文件夹数量
     */
    private Integer folderCount;
}

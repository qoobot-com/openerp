package com.qoobot.qooerp.file.dto;

import lombok.Data;

import java.util.Map;

/**
 * 存储统计DTO
 *
 * @author QooERP
 * @date 2026-02-17
 */
@Data
public class StorageStatisticsDTO {

    /**
     * 总文件数
     */
    private Long totalFiles;

    /**
     * 总文件夹数
     */
    private Long totalFolders;

    /**
     * 总存储空间（字节）
     */
    private Long totalStorage;

    /**
     * 今日上传数
     */
    private Long todayUploads;

    /**
     * 今日下载数
     */
    private Long todayDownloads;

    /**
     * 文件类型统计
     */
    private Map<String, Long> fileTypeStats;

    /**
     * 存储趋势（最近7天）
     */
    private Map<String, Long> storageTrend;
}

package com.qoobot.qooerp.file.service;

import com.qoobot.qooerp.file.dto.FileQuotaDTO;
import com.qoobot.qooerp.file.dto.StorageStatisticsDTO;

/**
 * 存储配额服务接口
 *
 * @author QooERP
 * @date 2026-02-17
 */
public interface FileQuotaService {

    /**
     * 获取配额信息
     *
     * @return 配额信息
     */
    FileQuotaDTO getQuotaInfo();

    /**
     * 获取存储统计
     *
     * @return 存储统计
     */
    StorageStatisticsDTO getStatistics();

    /**
     * 清理过期文件
     *
     * @param days 天数
     */
    void cleanup(Integer days);

    /**
     * 更新配额使用量
     *
     * @param fileSize 文件大小（增加时为正，删除时为负）
     */
    void updateUsedQuota(Long fileSize);

    /**
     * 检查配额是否足够
     *
     * @param fileSize 文件大小
     * @return 是否足够
     */
    boolean checkQuota(Long fileSize);
}

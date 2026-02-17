package com.qoobot.qooerp.file.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qoobot.qooerp.common.exception.BusinessException;
import com.qoobot.qooerp.common.tenant.TenantContextHolder;
import com.qoobot.qooerp.file.dto.FileQuotaDTO;
import com.qoobot.qooerp.file.dto.StorageStatisticsDTO;
import com.qoobot.qooerp.file.entity.FileInfo;
import com.qoobot.qooerp.file.entity.FileFolder;
import com.qoobot.qooerp.file.entity.FileQuota;
import com.qoobot.qooerp.file.mapper.FileInfoMapper;
import com.qoobot.qooerp.file.mapper.FileFolderMapper;
import com.qoobot.qooerp.file.mapper.FileQuotaMapper;
import com.qoobot.qooerp.file.service.FileQuotaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 存储配额服务实现
 *
 * @author QooERP
 * @date 2026-02-17
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileQuotaServiceImpl implements FileQuotaService {

    private final FileQuotaMapper quotaMapper;
    private final FileInfoMapper fileInfoMapper;
    private final FileFolderMapper fileFolderMapper;

    @Override
    public FileQuotaDTO getQuotaInfo() {
        Long tenantId = TenantContextHolder.getTenantId();
        FileQuota quota = quotaMapper.selectByTenantId(tenantId);

        if (quota == null) {
            // 初始化配额
            quota = new FileQuota();
            quota.setTenantId(tenantId);
            quota.setTotalQuota(107374182400L); // 100GB
            quota.setUsedQuota(0L);
            quota.setFileCount(0);
            quota.setFolderCount(0);
            quotaMapper.insert(quota);
        }

        FileQuotaDTO dto = new FileQuotaDTO();
        dto.setTotalQuota(quota.getTotalQuota());
        dto.setUsedQuota(quota.getUsedQuota());
        dto.setRemainingQuota(quota.getTotalQuota() - quota.getUsedQuota());
        dto.setUsageRate((double) quota.getUsedQuota() / quota.getTotalQuota() * 100);
        dto.setFileCount(quota.getFileCount());
        dto.setFolderCount(quota.getFolderCount());

        return dto;
    }

    @Override
    public StorageStatisticsDTO getStatistics() {
        Long tenantId = TenantContextHolder.getTenantId();

        StorageStatisticsDTO dto = new StorageStatisticsDTO();

        // 统计文件数
        LambdaQueryWrapper<FileInfo> fileWrapper = new LambdaQueryWrapper<>();
        fileWrapper.eq(FileInfo::getTenantId, tenantId);
        dto.setTotalFiles(fileInfoMapper.selectCount(fileWrapper));

        // 统计文件夹数
        LambdaQueryWrapper<FileFolder> folderWrapper = new LambdaQueryWrapper<>();
        folderWrapper.eq(FileFolder::getTenantId, tenantId);
        dto.setTotalFolders((long) fileFolderMapper.selectCount(folderWrapper));

        // 统计总存储
        dto.setTotalStorage(getQuotaInfo().getUsedQuota());

        // 统计今日上传
        dto.setTodayUploads(fileInfoMapper.countTodayUploads(tenantId));

        // 统计今日下载
        dto.setTodayDownloads(fileInfoMapper.countTodayDownloads(tenantId));

        // 文件类型统计
        List<Map<String, Object>> typeStats = fileInfoMapper.countByFileType(tenantId);
        Map<String, Long> fileTypeStats = new HashMap<>();
        for (Map<String, Object> stat : typeStats) {
            String type = (String) stat.get("file_type");
            Long count = ((Number) stat.get("count")).longValue();
            fileTypeStats.put(type, count);
        }
        dto.setFileTypeStats(fileTypeStats);

        return dto;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cleanup(Integer days) {
        // TODO: 实现清理过期文件逻辑
        log.info("清理{}天前的文件", days);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUsedQuota(Long fileSize) {
        Long tenantId = TenantContextHolder.getTenantId();
        FileQuota quota = quotaMapper.selectByTenantId(tenantId);

        if (quota == null) {
            quota = new FileQuota();
            quota.setTenantId(tenantId);
            quota.setTotalQuota(107374182400L);
            quota.setUsedQuota(0L);
            quota.setFileCount(0);
            quota.setFolderCount(0);
        }

        quota.setUsedQuota(quota.getUsedQuota() + fileSize);
        if (fileSize > 0) {
            quota.setFileCount(quota.getFileCount() + 1);
        } else {
            quota.setFileCount(Math.max(0, quota.getFileCount() - 1));
        }

        if (quota.getId() == null) {
            quotaMapper.insert(quota);
        } else {
            quotaMapper.updateById(quota);
        }
    }

    @Override
    public boolean checkQuota(Long fileSize) {
        FileQuotaDTO quota = getQuotaInfo();
        return quota.getRemainingQuota() >= fileSize;
    }
}

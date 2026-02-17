package com.qoobot.qooerp.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.qoobot.qooerp.system.entity.SystemBackup;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 系统备份服务接口
 */
public interface SystemBackupService {

    /**
     * 创建全量备份
     *
     * @param description 备份描述
     * @return 备份ID
     */
    Long createFullBackup(String description);

    /**
     * 创建增量备份
     *
     * @param baseBackupId 基础备份ID
     * @param description 备份描述
     * @return 备份ID
     */
    Long createIncrementalBackup(Long baseBackupId, String description);

    /**
     * 创建差异备份
     *
     * @param fullBackupId 全量备份ID
     * @param description 备份描述
     * @return 备份ID
     */
    Long createDifferentialBackup(Long fullBackupId, String description);

    /**
     * 执行备份恢复
     *
     * @param backupId 备份ID
     * @return 是否成功
     */
    Boolean performRestore(Long backupId);

    /**
     * 删除备份
     *
     * @param backupId 备份ID
     * @return 是否成功
     */
    Boolean deleteBackup(Long backupId);

    /**
     * 获取备份详情
     *
     * @param backupId 备份ID
     * @return 备份详情
     */
    SystemBackup getBackupDetail(Long backupId);

    /**
     * 分页查询备份列表
     *
     * @param current 当前页
     * @param size 每页大小
     * @param backupType 备份类型
     * @param status 状态
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 分页结果
     */
    IPage<SystemBackup> pageBackups(Long current, Long size, String backupType,
                                   String status, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 获取所有可用备份
     *
     * @return 备份列表
     */
    List<SystemBackup> getAvailableBackups();

    /**
     * 清理过期备份
     *
     * @return 清理的备份数量
     */
    Integer cleanExpiredBackups();

    /**
     * 设置备份策略
     *
     * @param backupType 备份类型
     * @param cronExpression Cron表达式
     * @param retentionDays 保留天数
     * @return 是否成功
     */
    Boolean setBackupStrategy(String backupType, String cronExpression, Integer retentionDays);

    /**
     * 获取备份策略
     *
     * @return 备份策略
     */
    Map<String, Object> getBackupStrategy();
}

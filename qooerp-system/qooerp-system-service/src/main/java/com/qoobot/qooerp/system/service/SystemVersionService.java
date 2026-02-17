package com.qoobot.qooerp.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.qoobot.qooerp.system.entity.SystemVersion;

import java.util.List;

/**
 * 版本管理服务接口
 */
public interface SystemVersionService {

    /**
     * 创建版本
     *
     * @param version 版本实体
     * @return 版本ID
     */
    Long createVersion(SystemVersion version);

    /**
     * 更新版本
     *
     * @param version 版本实体
     * @return 是否成功
     */
    Boolean updateVersion(SystemVersion version);

    /**
     * 删除版本
     *
     * @param versionId 版本ID
     * @return 是否成功
     */
    Boolean deleteVersion(Long versionId);

    /**
     * 获取版本详情
     *
     * @param versionId 版本ID
     * @return 版本详情
     */
    SystemVersion getVersionDetail(Long versionId);

    /**
     * 获取当前版本
     *
     * @return 当前版本
     */
    SystemVersion getCurrentVersion();

    /**
     * 获取最新版本
     *
     * @return 最新版本
     */
    SystemVersion getLatestVersion();

    /**
     * 分页查询版本列表
     *
     * @param current 当前页
     * @param size 每页大小
     * @param versionType 版本类型
     * @param status 状态
     * @return 分页结果
     */
    IPage<SystemVersion> pageVersions(Long current, Long size, String versionType, String status);

    /**
     * 发布版本
     *
     * @param versionId 版本ID
     * @return 是否成功
     */
    Boolean publishVersion(Long versionId);

    /**
     * 回滚版本
     *
     * @param versionId 目标版本ID
     * @return 是否成功
     */
    Boolean rollbackVersion(Long versionId);

    /**
     * 灰度发布
     *
     * @param versionId 版本ID
     * @param canaryInstances 灰度实例数
     * @return 是否成功
     */
    Boolean canaryDeploy(Long versionId, Integer canaryInstances);

    /**
     * 完成灰度发布
     *
     * @param versionId 版本ID
     * @return 是否成功
     */
    Boolean completeCanaryDeploy(Long versionId);

    /**
     * 获取所有版本
     *
     * @return 版本列表
     */
    List<SystemVersion> getAllVersions();

    /**
     * 检查版本更新
     *
     * @return 是否有更新
     */
    Boolean checkVersionUpdate();
}

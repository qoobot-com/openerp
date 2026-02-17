package com.qoobot.qooerp.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qoobot.qooerp.system.entity.SystemVersion;
import com.qoobot.qooerp.system.mapper.SystemVersionMapper;
import com.qoobot.qooerp.system.service.SystemVersionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 版本管理服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SystemVersionServiceImpl extends ServiceImpl<SystemVersionMapper, SystemVersion>
        implements SystemVersionService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createVersion(SystemVersion version) {
        save(version);
        log.info("创建版本: version={}", version.getVersion());
        return version.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateVersion(SystemVersion version) {
        return updateById(version);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteVersion(Long versionId) {
        return removeById(versionId);
    }

    @Override
    public SystemVersion getVersionDetail(Long versionId) {
        return getById(versionId);
    }

    @Override
    public SystemVersion getCurrentVersion() {
        return getOne(new LambdaQueryWrapper<SystemVersion>()
                .eq(SystemVersion::getStatus, "ROLLED_OUT")
                .orderByDesc(SystemVersion::getPublishTime)
                .last("LIMIT 1"));
    }

    @Override
    public SystemVersion getLatestVersion() {
        return getOne(new LambdaQueryWrapper<SystemVersion>()
                .in(SystemVersion::getStatus, List.of("PUBLISHED", "ROLLED_OUT"))
                .orderByDesc(SystemVersion::getPublishTime)
                .last("LIMIT 1"));
    }

    @Override
    public IPage<SystemVersion> pageVersions(Long current, Long size, String versionType, String status) {
        Page<SystemVersion> page = new Page<>(current, size);
        LambdaQueryWrapper<SystemVersion> wrapper = new LambdaQueryWrapper<>();

        if (versionType != null) {
            wrapper.eq(SystemVersion::getVersionType, versionType);
        }
        if (status != null) {
            wrapper.eq(SystemVersion::getStatus, status);
        }

        wrapper.orderByDesc(SystemVersion::getPublishTime);

        return page(page, wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean publishVersion(Long versionId) {
        SystemVersion version = getById(versionId);
        if (version == null) {
            return false;
        }

        version.setStatus("PUBLISHED");
        version.setPublishTime(LocalDateTime.now());
        updateById(version);

        log.info("发布版本: version={}", version.getVersion());
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean rollbackVersion(Long versionId) {
        SystemVersion version = getById(versionId);
        if (version == null) {
            return false;
        }

        // 执行回滚（实际实现应调用回滚脚本）
        version.setStatus("ROLLED_BACK");
        version.setRollbackTime(LocalDateTime.now());
        updateById(version);

        log.info("回滚版本: version={}", version.getVersion());
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean canaryDeploy(Long versionId, Integer canaryInstances) {
        SystemVersion version = getById(versionId);
        if (version == null) {
            return false;
        }

        version.setCanaryStatus("ROLLING_OUT");
        version.setCanaryInstances(canaryInstances);
        updateById(version);

        log.info("灰度发布: version={}, canaryInstances={}", version.getVersion(), canaryInstances);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean completeCanaryDeploy(Long versionId) {
        SystemVersion version = getById(versionId);
        if (version == null) {
            return false;
        }

        version.setCanaryStatus("ROLLED_OUT");
        version.setStatus("PUBLISHED");
        version.setPublishTime(LocalDateTime.now());
        updateById(version);

        log.info("完成灰度发布: version={}", version.getVersion());
        return true;
    }

    @Override
    public List<SystemVersion> getAllVersions() {
        return list(new LambdaQueryWrapper<SystemVersion>()
                .orderByDesc(SystemVersion::getPublishTime)
                .last("LIMIT 100"));
    }

    @Override
    public Boolean checkVersionUpdate() {
        // 检查版本更新（实际实现应连接到版本服务器）
        return false;
    }
}

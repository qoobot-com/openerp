package com.qoobot.qooerp.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qoobot.qooerp.system.dto.SystemAnnouncementDTO;
import com.qoobot.qooerp.system.dto.SystemAnnouncementQueryDTO;
import com.qoobot.qooerp.system.entity.SystemAnnouncement;
import com.qoobot.qooerp.system.entity.SystemAnnouncementRead;
import com.qoobot.qooerp.system.mapper.SystemAnnouncementMapper;
import com.qoobot.qooerp.system.mapper.SystemAnnouncementReadMapper;
import com.qoobot.qooerp.system.service.SystemAnnouncementService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 系统公告Service实现
 */
@Service
public class SystemAnnouncementServiceImpl extends ServiceImpl<SystemAnnouncementMapper, SystemAnnouncement> implements SystemAnnouncementService {

    @Autowired
    private SystemAnnouncementReadMapper announcementReadMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SystemAnnouncement create(SystemAnnouncementDTO dto) {
        SystemAnnouncement announcement = new SystemAnnouncement();
        BeanUtils.copyProperties(dto, announcement);
        announcement.setStatus(0); // 默认为草稿
        announcement.setReadCount(0);
        this.save(announcement);
        return announcement;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SystemAnnouncement update(SystemAnnouncementDTO dto) {
        SystemAnnouncement announcement = this.getById(dto.getId());
        if (announcement == null) {
            throw new RuntimeException("公告不存在");
        }
        BeanUtils.copyProperties(dto, announcement);
        this.updateById(announcement);
        return announcement;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        SystemAnnouncement announcement = this.getById(id);
        if (announcement == null) {
            throw new RuntimeException("公告不存在");
        }
        if (announcement.getStatus() == 1) {
            throw new RuntimeException("已发布的公告不能删除");
        }
        this.removeById(id);
    }

    @Override
    public SystemAnnouncement getById(Long id) {
        return super.getById(id);
    }

    @Override
    public IPage<SystemAnnouncement> page(SystemAnnouncementQueryDTO query) {
        Page<SystemAnnouncement> page = new Page<>(query.getCurrent(), query.getSize());
        return this.baseMapper.selectPageByQuery(page, query);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void publish(Long id) {
        SystemAnnouncement announcement = this.getById(id);
        if (announcement == null) {
            throw new RuntimeException("公告不存在");
        }
        if (announcement.getStatus() == 1) {
            throw new RuntimeException("公告已发布");
        }
        announcement.setStatus(1);
        announcement.setPublishTime(LocalDateTime.now());
        this.updateById(announcement);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void revoke(Long id) {
        SystemAnnouncement announcement = this.getById(id);
        if (announcement == null) {
            throw new RuntimeException("公告不存在");
        }
        if (announcement.getStatus() != 1) {
            throw new RuntimeException("只有已发布的公告才能撤回");
        }
        announcement.setStatus(2);
        this.updateById(announcement);
    }

    @Override
    public List<SystemAnnouncement> getMyAnnouncements(Long userId) {
        // TODO: 根据用户角色和部门过滤公告
        LambdaQueryWrapper<SystemAnnouncement> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SystemAnnouncement::getStatus, 1);
        wrapper.orderByDesc(SystemAnnouncement::getPriority, SystemAnnouncement::getPublishTime);
        return this.list(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markAsRead(Long announcementId, Long userId) {
        // 检查是否已存在阅读记录
        LambdaQueryWrapper<SystemAnnouncementRead> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SystemAnnouncementRead::getAnnouncementId, announcementId);
        wrapper.eq(SystemAnnouncementRead::getUserId, userId);
        SystemAnnouncementRead readRecord = announcementReadMapper.selectOne(wrapper);

        if (readRecord == null) {
            // 创建新的阅读记录
            readRecord = new SystemAnnouncementRead();
            readRecord.setAnnouncementId(announcementId);
            readRecord.setUserId(userId);
            readRecord.setIsRead(false);
            announcementReadMapper.insert(readRecord);
        }

        if (!readRecord.getIsRead()) {
            readRecord.setIsRead(true);
            readRecord.setReadTime(LocalDateTime.now());
            announcementReadMapper.updateById(readRecord);

            // 更新公告阅读次数
            SystemAnnouncement announcement = this.getById(announcementId);
            announcement.setReadCount(announcement.getReadCount() + 1);
            this.updateById(announcement);
        }
    }
}

package com.qoobot.qooerp.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qoobot.qooerp.system.dto.SystemAnnouncementDTO;
import com.qoobot.qooerp.system.dto.SystemAnnouncementQueryDTO;
import com.qoobot.qooerp.system.entity.SystemAnnouncement;

import java.util.List;

/**
 * 系统公告Service
 */
public interface SystemAnnouncementService extends IService<SystemAnnouncement> {

    /**
     * 创建公告
     */
    SystemAnnouncement create(SystemAnnouncementDTO dto);

    /**
     * 更新公告
     */
    SystemAnnouncement update(SystemAnnouncementDTO dto);

    /**
     * 删除公告
     */
    void delete(Long id);

    /**
     * 获取公告详情
     */
    SystemAnnouncement getById(Long id);

    /**
     * 分页查询公告
     */
    IPage<SystemAnnouncement> page(SystemAnnouncementQueryDTO query);

    /**
     * 发布公告
     */
    void publish(Long id);

    /**
     * 撤回公告
     */
    void revoke(Long id);

    /**
     * 获取我的公告列表
     */
    List<SystemAnnouncement> getMyAnnouncements(Long userId);

    /**
     * 标记公告已读
     */
    void markAsRead(Long announcementId, Long userId);
}

package com.qoobot.qooerp.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.qoobot.qooerp.system.dto.SystemAnnouncementDTO;
import com.qoobot.qooerp.system.dto.SystemAnnouncementQueryDTO;
import com.qoobot.qooerp.system.entity.SystemAnnouncement;
import com.qoobot.qooerp.system.service.SystemAnnouncementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 系统公告Controller
 */
@RestController
@RequestMapping("/api/system/announcement")
public class SystemAnnouncementController {

    @Autowired
    private SystemAnnouncementService announcementService;

    /**
     * 创建公告
     */
    @PostMapping
    public SystemAnnouncement create(@RequestBody SystemAnnouncementDTO dto) {
        return announcementService.create(dto);
    }

    /**
     * 获取公告详情
     */
    @GetMapping("/{id}")
    public SystemAnnouncement getById(@PathVariable Long id) {
        return announcementService.getById(id);
    }

    /**
     * 更新公告
     */
    @PutMapping
    public SystemAnnouncement update(@RequestBody SystemAnnouncementDTO dto) {
        return announcementService.update(dto);
    }

    /**
     * 删除公告
     */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        announcementService.delete(id);
    }

    /**
     * 分页查询公告
     */
    @PostMapping("/page")
    public IPage<SystemAnnouncement> page(@RequestBody SystemAnnouncementQueryDTO query) {
        return announcementService.page(query);
    }

    /**
     * 发布公告
     */
    @PostMapping("/{id}/publish")
    public void publish(@PathVariable Long id) {
        announcementService.publish(id);
    }

    /**
     * 撤回公告
     */
    @PostMapping("/{id}/revoke")
    public void revoke(@PathVariable Long id) {
        announcementService.revoke(id);
    }

    /**
     * 获取我的公告列表
     */
    @GetMapping("/my")
    public List<SystemAnnouncement> getMyAnnouncements(@RequestParam Long userId) {
        return announcementService.getMyAnnouncements(userId);
    }

    /**
     * 标记公告已读
     */
    @PostMapping("/{id}/read")
    public void markAsRead(@PathVariable Long id, @RequestParam Long userId) {
        announcementService.markAsRead(id, userId);
    }
}

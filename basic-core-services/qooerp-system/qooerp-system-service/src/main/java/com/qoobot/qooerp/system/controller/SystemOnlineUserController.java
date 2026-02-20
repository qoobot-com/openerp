package com.qoobot.qooerp.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.qoobot.qooerp.system.dto.SystemOnlineUserQueryDTO;
import com.qoobot.qooerp.system.entity.SystemOnlineUser;
import com.qoobot.qooerp.system.service.SystemOnlineUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 在线用户Controller
 */
@RestController
@RequestMapping("/api/system/online-user")
public class SystemOnlineUserController {

    @Autowired
    private SystemOnlineUserService onlineUserService;

    /**
     * 分页查询在线用户
     */
    @PostMapping("/page")
    public IPage<SystemOnlineUser> page(@RequestBody SystemOnlineUserQueryDTO query) {
        return onlineUserService.page(query);
    }

    /**
     * 强制下线
     */
    @PostMapping("/logout")
    public void forceLogout(@RequestParam String token) {
        onlineUserService.forceLogout(token);
    }

    /**
     * 批量强制下线
     */
    @PostMapping("/logout/batch")
    public void batchForceLogout(@RequestBody String[] tokens) {
        onlineUserService.batchForceLogout(tokens);
    }

    /**
     * 获取在线统计
     */
    @GetMapping("/stats")
    public Map<String, Object> getOnlineStats() {
        return onlineUserService.getOnlineStats();
    }
}

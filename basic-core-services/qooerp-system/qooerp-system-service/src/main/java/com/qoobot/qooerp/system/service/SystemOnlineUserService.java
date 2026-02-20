package com.qoobot.qooerp.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qoobot.qooerp.system.dto.SystemOnlineUserQueryDTO;
import com.qoobot.qooerp.system.entity.SystemOnlineUser;

import java.util.Map;

/**
 * 在线用户Service
 */
public interface SystemOnlineUserService extends IService<SystemOnlineUser> {

    /**
     * 分页查询在线用户
     */
    IPage<SystemOnlineUser> page(SystemOnlineUserQueryDTO query);

    /**
     * 强制下线
     */
    void forceLogout(String token);

    /**
     * 批量强制下线
     */
    void batchForceLogout(String[] tokens);

    /**
     * 获取在线统计
     */
    Map<String, Object> getOnlineStats();
}

package com.qoobot.qooerp.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qoobot.qooerp.system.dto.SystemOnlineUserQueryDTO;
import com.qoobot.qooerp.system.entity.SystemOnlineUser;
import com.qoobot.qooerp.system.mapper.SystemOnlineUserMapper;
import com.qoobot.qooerp.system.service.SystemOnlineUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 在线用户Service实现
 */
@Service
public class SystemOnlineUserServiceImpl extends ServiceImpl<SystemOnlineUserMapper, SystemOnlineUser> implements SystemOnlineUserService {

    @Override
    public IPage<SystemOnlineUser> page(SystemOnlineUserQueryDTO query) {
        Page<SystemOnlineUser> page = new Page<>(query.getCurrent(), query.getSize());
        LambdaQueryWrapper<SystemOnlineUser> wrapper = new LambdaQueryWrapper<>();

        if (query.getUsername() != null && !query.getUsername().isEmpty()) {
            wrapper.like(SystemOnlineUser::getUsername, query.getUsername());
        }
        if (query.getLoginIp() != null && !query.getLoginIp().isEmpty()) {
            wrapper.like(SystemOnlineUser::getLoginIp, query.getLoginIp());
        }
        if (query.getStatus() != null) {
            wrapper.eq(SystemOnlineUser::getStatus, query.getStatus());
        }

        wrapper.orderByDesc(SystemOnlineUser::getLoginTime);
        return this.page(page, wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void forceLogout(String token) {
        SystemOnlineUser onlineUser = this.getOne(
            new LambdaQueryWrapper<SystemOnlineUser>()
                .eq(SystemOnlineUser::getToken, token)
        );
        if (onlineUser != null) {
            onlineUser.setStatus(0);
            this.updateById(onlineUser);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchForceLogout(String[] tokens) {
        if (tokens == null || tokens.length == 0) {
            return;
        }
        List<SystemOnlineUser> onlineUsers = this.list(
            new LambdaQueryWrapper<SystemOnlineUser>()
                .in(SystemOnlineUser::getToken, List.of(tokens))
        );
        onlineUsers.forEach(user -> user.setStatus(0));
        this.updateBatchById(onlineUsers);
    }

    @Override
    public Map<String, Object> getOnlineStats() {
        Map<String, Object> stats = new HashMap<>();

        long onlineCount = this.count(
            new LambdaQueryWrapper<SystemOnlineUser>()
                .eq(SystemOnlineUser::getStatus, 1)
        );
        long offlineCount = this.count(
            new LambdaQueryWrapper<SystemOnlineUser>()
                .eq(SystemOnlineUser::getStatus, 0)
        );
        long totalCount = this.count();

        stats.put("onlineCount", onlineCount);
        stats.put("offlineCount", offlineCount);
        stats.put("totalCount", totalCount);

        return stats;
    }
}

package com.qoobot.qooerp.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qoobot.qooerp.permission.entity.PermissionUserRole;
import com.qoobot.qooerp.permission.mapper.PermissionUserRoleMapper;
import com.qoobot.qooerp.user.service.UserRoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户角色服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserRoleServiceImpl implements UserRoleService {

    private final PermissionUserRoleMapper userRoleMapper;
    private static final String USER_ROLES_CACHE_PREFIX = "user:roles:";

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = USER_ROLES_CACHE_PREFIX, key = "#userId")
    public boolean assignRoles(Long userId, List<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return true;
        }

        // 删除旧的角色关联
        removeRoles(userId, null);

        // 添加新的角色关联
        int rows = 0;
        for (Long roleId : roleIds) {
            PermissionUserRole userRole = new PermissionUserRole();
            userRole.setUserId(userId);
            userRole.setRoleId(roleId);
            if (userRoleMapper.insert(userRole) > 0) {
                rows++;
            }
        }

        log.info("分配用户角色成功，userId={}, roleIds={}, count={}", userId, roleIds, rows);
        return rows == roleIds.size();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = USER_ROLES_CACHE_PREFIX, key = "#userId")
    public boolean removeRoles(Long userId, List<Long> roleIds) {
        LambdaQueryWrapper<PermissionUserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PermissionUserRole::getUserId, userId);

        if (roleIds != null && !roleIds.isEmpty()) {
            wrapper.in(PermissionUserRole::getRoleId, roleIds);
        }

        int rows = userRoleMapper.delete(wrapper);
        log.info("移除用户角色成功，userId={}, roleIds={}, count={}", userId, roleIds, rows);
        return rows >= 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = USER_ROLES_CACHE_PREFIX, key = "#userId")
    public boolean updateRoles(Long userId, List<Long> roleIds) {
        return assignRoles(userId, roleIds);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = USER_ROLES_CACHE_PREFIX, key = "#userId")
    public boolean clearRoles(Long userId) {
        return removeRoles(userId, null);
    }

    @Override
    public List<Long> getRoleIds(Long userId) {
        LambdaQueryWrapper<PermissionUserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PermissionUserRole::getUserId, userId);
        wrapper.select(PermissionUserRole::getRoleId);

        return userRoleMapper.selectList(wrapper).stream()
                .map(PermissionUserRole::getRoleId)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchAssignRoles(List<Long> userIds, List<Long> roleIds) {
        if (userIds == null || userIds.isEmpty() || roleIds == null || roleIds.isEmpty()) {
            return true;
        }

        int totalRows = 0;
        for (Long userId : userIds) {
            if (assignRoles(userId, roleIds)) {
                totalRows++;
            }
        }
        log.info("批量分配用户角色成功，userIds={}, roleIds={}, count={}", userIds, roleIds, totalRows);
        return totalRows == userIds.size();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchRemoveRoles(List<Long> userIds, List<Long> roleIds) {
        if (userIds == null || userIds.isEmpty()) {
            return true;
        }

        int totalRows = 0;
        for (Long userId : userIds) {
            if (removeRoles(userId, roleIds)) {
                totalRows++;
            }
        }
        log.info("批量移除用户角色成功，userIds={}, roleIds={}, count={}", userIds, roleIds, totalRows);
        return totalRows == userIds.size();
    }
}

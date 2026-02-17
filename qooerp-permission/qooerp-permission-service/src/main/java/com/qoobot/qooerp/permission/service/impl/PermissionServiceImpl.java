package com.qoobot.qooerp.permission.service.impl;

import com.qoobot.qooerp.common.exception.BusinessException;
import com.qoobot.qooerp.common.util.SecurityUtils;
import com.qoobot.qooerp.permission.entity.PermissionMenu;
import com.qoobot.qooerp.permission.service.PermissionMenuService;
import com.qoobot.qooerp.permission.service.PermissionRoleService;
import com.qoobot.qooerp.permission.service.PermissionService;
import com.qoobot.qooerp.permission.service.PermissionUserService;
import com.qoobot.qooerp.permission.vo.PermissionMenuVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 权限Service实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRoleService roleService;
    private final PermissionMenuService menuService;
    private final PermissionUserService userService;

    @Override
    public Boolean checkPermission(Long userId, String permission) {
        if (!hasPermission(userId, permission)) {
            throw new BusinessException("没有权限访问");
        }
        return true;
    }

    @Override
    public Boolean hasPermission(Long userId, String permission) {
        return userService.hasPermission(userId, permission);
    }

    @Override
    public List<String> getCurrentUserPermissions() {
        Long userId = SecurityUtils.getUserId();
        if (userId == null) {
            return List.of();
        }

        // 获取用户角色
        var roles = roleService.getRolesByUserId(userId);
        if (roles.isEmpty()) {
            return List.of();
        }

        // 检查是否是超级管理员
        boolean isAdmin = roles.stream().anyMatch(role ->
                "ADMIN".equals(role.getRoleCode()));
        if (isAdmin) {
            return List.of("*"); // 超级管理员拥有所有权限
        }

        // 获取角色的所有菜单权限
        Set<Long> roleIds = roles.stream().map(r -> r.getId()).collect(Collectors.toSet());
        return roleIds.stream()
                .flatMap(roleId -> menuService.getMenusByRoleId(roleId).stream())
                .map(PermissionMenu::getPermission)
                .filter(p -> p != null && !p.isEmpty())
                .collect(Collectors.toList());
    }

    @Override
    public List<PermissionMenuVO> getCurrentUserMenuTree() {
        Long userId = SecurityUtils.getUserId();
        if (userId == null) {
            return List.of();
        }
        return userService.getUserMenuTree(userId);
    }

    @Override
    public void refreshUserCache(Long userId) {
        userService.clearUserCache(userId);
        log.info("刷新用户权限缓存: userId={}", userId);
    }

    @Override
    public void clearAllCache() {
        roleService.clearCache();
        menuService.clearCache();
        log.info("清除所有权限缓存");
    }
}

package com.qoobot.qooerp.permission.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qoobot.qooerp.permission.constants.PermissionConstants;
import com.qoobot.qooerp.permission.dto.PermissionUserDTO;
import com.qoobot.qooerp.permission.entity.PermissionRole;
import com.qoobot.qooerp.permission.entity.PermissionUserRole;
import com.qoobot.qooerp.permission.mapper.PermissionUserRoleMapper;
import com.qoobot.qooerp.permission.service.PermissionMenuService;
import com.qoobot.qooerp.permission.service.PermissionRoleService;
import com.qoobot.qooerp.permission.service.PermissionUserService;
import com.qoobot.qooerp.permission.vo.PermissionMenuVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 用户权限Service实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PermissionUserServiceImpl implements PermissionUserService {

    private final PermissionUserRoleMapper userRoleMapper;
    private final PermissionRoleService roleService;
    private final PermissionMenuService menuService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = {PermissionConstants.CACHE_USER_ROLE, PermissionConstants.CACHE_USER_MENU}, key = "#dto.userId")
    public Boolean assignRolesToUser(PermissionUserDTO dto) {
        if (dto.getUserId() == null) {
            throw new RuntimeException("用户ID不能为空");
        }

        // 删除用户原有的角色
        userRoleMapper.deleteByUserId(dto.getUserId());

        // 批量插入新的用户角色关联
        if (!CollectionUtils.isEmpty(dto.getRoleIds())) {
            for (Long roleId : dto.getRoleIds()) {
                PermissionUserRole userRole = new PermissionUserRole();
                userRole.setUserId(dto.getUserId());
                userRole.setRoleId(roleId);
                userRoleMapper.insert(userRole);
            }
        }

        log.info("分配角色给用户成功: userId={}, roleIds={}", dto.getUserId(), dto.getRoleIds());
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = {PermissionConstants.CACHE_USER_ROLE, PermissionConstants.CACHE_USER_MENU}, allEntries = true)
    public Boolean deleteUserRoles(Long userId) {
        if (userId == null) {
            throw new RuntimeException("用户ID不能为空");
        }

        userRoleMapper.deleteByUserId(userId);
        log.info("删除用户角色成功: userId={}", userId);
        return true;
    }

    @Override
    @Cacheable(value = PermissionConstants.CACHE_USER_ROLE, key = "#userId")
    public List<PermissionRole> getUserRoles(Long userId) {
        return roleService.getRolesByUserId(userId);
    }

    @Override
    @Cacheable(value = PermissionConstants.CACHE_USER_MENU, key = "#userId")
    public List<PermissionMenuVO> getUserMenuTree(Long userId) {
        return menuService.getMenuTreeByUserId(userId);
    }

    @Override
    public Boolean hasPermission(Long userId, String permission) {
        // 查询用户的所有角色
        List<PermissionRole> roles = roleService.getRolesByUserId(userId);
        if (CollectionUtils.isEmpty(roles)) {
            return false;
        }

        // 检查是否是超级管理员
        boolean isAdmin = roles.stream().anyMatch(role ->
                PermissionConstants.ADMIN_ROLE_CODE.equals(role.getRoleCode()));
        if (isAdmin) {
            return true;
        }

        // 获取角色的所有菜单
        Set<Long> roleIds = roles.stream().map(PermissionRole::getId).collect(Collectors.toSet());
        Set<String> permissions = roleIds.stream()
                .flatMap(roleId -> menuService.getMenusByRoleId(roleId).stream())
                .map(menu -> menu.getPermission())
                .filter(p -> p != null)
                .collect(Collectors.toSet());

        return permissions.contains(permission);
    }

    @Override
    @CacheEvict(value = {PermissionConstants.CACHE_USER_ROLE, PermissionConstants.CACHE_USER_MENU}, key = "#userId")
    public void clearUserCache(Long userId) {
        log.info("清除用户缓存成功: userId={}", userId);
    }
}

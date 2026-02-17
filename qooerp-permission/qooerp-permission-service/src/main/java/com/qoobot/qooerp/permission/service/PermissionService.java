package com.qoobot.qooerp.permission.service;

import com.qoobot.qooerp.permission.vo.PermissionMenuVO;

import java.util.List;

/**
 * 权限Service接口
 */
public interface PermissionService {

    /**
     * 验证用户权限
     *
     * @param userId 用户ID
     * @param permission 权限标识
     * @return 是否拥有权限
     */
    Boolean checkPermission(Long userId, String permission);

    /**
     * 验证用户权限（不抛出异常）
     *
     * @param userId 用户ID
     * @param permission 权限标识
     * @return 是否拥有权限
     */
    Boolean hasPermission(Long userId, String permission);

    /**
     * 获取当前用户权限标识列表
     *
     * @return 权限标识列表
     */
    List<String> getCurrentUserPermissions();

    /**
     * 获取当前用户菜单树
     *
     * @return 菜单树
     */
    List<PermissionMenuVO> getCurrentUserMenuTree();

    /**
     * 刷新用户权限缓存
     *
     * @param userId 用户ID
     */
    void refreshUserCache(Long userId);

    /**
     * 清除所有权限缓存
     */
    void clearAllCache();
}

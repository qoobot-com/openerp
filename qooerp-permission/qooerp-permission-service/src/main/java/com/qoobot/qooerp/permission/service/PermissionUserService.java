package com.qoobot.qooerp.permission.service;

import com.qoobot.qooerp.permission.dto.PermissionUserDTO;
import com.qoobot.qooerp.permission.entity.PermissionRole;
import com.qoobot.qooerp.permission.vo.PermissionMenuVO;

import java.util.List;

/**
 * 用户权限Service接口
 */
public interface PermissionUserService {

    /**
     * 分配角色给用户
     *
     * @param dto 用户角色DTO
     * @return 是否成功
     */
    Boolean assignRolesToUser(PermissionUserDTO dto);

    /**
     * 删除用户的所有角色
     *
     * @param userId 用户ID
     * @return 是否成功
     */
    Boolean deleteUserRoles(Long userId);

    /**
     * 获取用户的角色列表
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    List<PermissionRole> getUserRoles(Long userId);

    /**
     * 获取用户的菜单树
     *
     * @param userId 用户ID
     * @return 菜单树
     */
    List<PermissionMenuVO> getUserMenuTree(Long userId);

    /**
     * 检查用户是否拥有指定权限
     *
     * @param userId 用户ID
     * @param permission 权限标识
     * @return 是否拥有权限
     */
    Boolean hasPermission(Long userId, String permission);

    /**
     * 清除用户缓存
     *
     * @param userId 用户ID
     */
    void clearUserCache(Long userId);
}

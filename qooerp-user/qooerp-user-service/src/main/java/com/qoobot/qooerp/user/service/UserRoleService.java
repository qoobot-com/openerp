package com.qoobot.qooerp.user.service;

import java.util.List;

/**
 * 用户角色服务接口
 */
public interface UserRoleService {

    /**
     * 分配角色
     */
    boolean assignRoles(Long userId, List<Long> roleIds);

    /**
     * 移除角色
     */
    boolean removeRoles(Long userId, List<Long> roleIds);

    /**
     * 更新用户角色
     */
    boolean updateRoles(Long userId, List<Long> roleIds);

    /**
     * 清空用户角色
     */
    boolean clearRoles(Long userId);

    /**
     * 获取用户角色ID列表
     */
    List<Long> getRoleIds(Long userId);

    /**
     * 批量分配角色
     */
    boolean batchAssignRoles(List<Long> userIds, List<Long> roleIds);

    /**
     * 批量移除角色
     */
    boolean batchRemoveRoles(List<Long> userIds, List<Long> roleIds);
}

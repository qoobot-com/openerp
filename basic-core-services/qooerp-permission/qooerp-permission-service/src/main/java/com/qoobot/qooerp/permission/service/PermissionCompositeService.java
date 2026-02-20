package com.qoobot.qooerp.permission.service;

import java.util.Map;
import java.util.Set;

/**
 * 复合权限服务接口（RBAC + ABAC）
 */
public interface PermissionCompositeService {

    /**
     * 验证权限（RBAC + ABAC组合）
     *
     * @param userId 用户ID
     * @param permission 权限标识
     * @param resourceAttributes 资源属性（用于ABAC评估）
     * @return 是否有权限
     */
    Boolean checkPermission(Long userId, String permission, Map<String, Object> resourceAttributes);

    /**
     * 验证权限（RBAC优先，ABAC作为补充）
     *
     * @param userId 用户ID
     * @param permission 权限标识
     * @param resourceType 资源类型
     * @param operationType 操作类型
     * @param resourceAttributes 资源属性
     * @return 是否有权限
     */
    Boolean checkPermissionWithAbac(Long userId, String permission, String resourceType,
                                      String operationType, Map<String, Object> resourceAttributes);

    /**
     * 获取用户的完整权限列表
     *
     * @param userId 用户ID
     * @return 权限标识集合
     */
    Set<String> getUserPermissions(Long userId);

    /**
     * 判断是否超级管理员
     *
     * @param userId 用户ID
     * @return 是否是超级管理员
     */
    Boolean isSuperAdmin(Long userId);

    /**
     * 设置权限评估模式
     *
     * @param mode 模式：RBAC_ONLY, ABAC_ONLY, RBAC_AND_ABAC, RBAC_OR_ABAC
     */
    void setPermissionMode(String mode);

    /**
     * 获取当前权限评估模式
     *
     * @return 权限评估模式
     */
    String getPermissionMode();
}

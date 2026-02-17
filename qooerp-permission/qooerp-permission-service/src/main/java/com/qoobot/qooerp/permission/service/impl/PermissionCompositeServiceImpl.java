package com.qoobot.qooerp.permission.service.impl;

import com.qoobot.qooerp.permission.entity.PermissionRole;
import com.qoobot.qooerp.permission.service.PermissionAbacService;
import com.qoobot.qooerp.permission.service.PermissionCompositeService;
import com.qoobot.qooerp.permission.service.PermissionService;
import com.qoobot.qooerp.permission.service.PermissionRoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 复合权限服务实现类（RBAC + ABAC）
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PermissionCompositeServiceImpl implements PermissionCompositeService {

    private final PermissionService rbacService;
    private final PermissionAbacService abacService;
    private final PermissionRoleService roleService;

    private static final String MODE_RBAC_ONLY = "RBAC_ONLY";
    private static final String MODE_ABAC_ONLY = "ABAC_ONLY";
    private static final String MODE_RBAC_AND_ABAC = "RBAC_AND_ABAC";
    private static final String MODE_RBAC_OR_ABAC = "RBAC_OR_ABAC";

    private String permissionMode = MODE_RBAC_AND_ABAC;

    @Override
    public Boolean checkPermission(Long userId, String permission, Map<String, Object> resourceAttributes) {
        return checkPermissionWithAbac(userId, permission, null, null, resourceAttributes);
    }

    @Override
    public Boolean checkPermissionWithAbac(Long userId, String permission, String resourceType,
                                              String operationType, Map<String, Object> resourceAttributes) {
        // 超级管理员拥有所有权限
        if (isSuperAdmin(userId)) {
            return true;
        }

        return switch (permissionMode) {
            case MODE_RBAC_ONLY -> checkRbacPermission(userId, permission);
            case MODE_ABAC_ONLY -> checkAbacPermission(userId, resourceType, operationType, resourceAttributes);
            case MODE_RBAC_AND_ABAC ->
                    checkRbacPermission(userId, permission) && checkAbacPermission(userId, resourceType, operationType, resourceAttributes);
            case MODE_RBAC_OR_ABAC ->
                    checkRbacPermission(userId, permission) || checkAbacPermission(userId, resourceType, operationType, resourceAttributes);
            default -> checkRbacPermission(userId, permission);
        };
    }

    @Override
    public Set<String> getUserPermissions(Long userId) {
        List<String> permissions = rbacService.getCurrentUserPermissions();
        return new HashSet<>(permissions);
    }

    @Override
    public Boolean isSuperAdmin(Long userId) {
        List<PermissionRole> roles = roleService.getRolesByUserId(userId);
        return roles.stream().anyMatch(role -> "ADMIN".equals(role.getRoleCode()));
    }

    @Override
    public void setPermissionMode(String mode) {
        this.permissionMode = mode;
        log.info("权限评估模式已设置为: {}", mode);
    }

    @Override
    public String getPermissionMode() {
        return permissionMode;
    }

    /**
     * 验证RBAC权限
     */
    private Boolean checkRbacPermission(Long userId, String permission) {
        return rbacService.hasPermission(userId, permission);
    }

    /**
     * 验证ABAC权限
     */
    private Boolean checkAbacPermission(Long userId, String resourceType, String operationType,
                                         Map<String, Object> resourceAttributes) {
        if (resourceType == null || operationType == null || resourceAttributes == null) {
            return false; // ABAC需要完整的上下文信息
        }

        try {
            return abacService.evaluateAbac(userId, resourceType, operationType, resourceAttributes);
        } catch (Exception e) {
            log.error("ABAC权限评估失败: userId={}, resourceType={}", userId, resourceType, e);
            return false;
        }
    }
}

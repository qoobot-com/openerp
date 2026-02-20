package com.qoobot.qooerp.common.util;

import com.qoobot.qooerp.common.security.AuthUserDetails;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * 安全工具类
 * 用于获取当前登录用户信息，基于JWT令牌解析
 *
 * @author QooERP Team
 * @version 1.0.0
 */
@Component
public class SecurityUtils {

    public static String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (auth != null && auth.isAuthenticated()) ? auth.getName() : null;
    }

    public static AuthUserDetails getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof AuthUserDetails) {
            return (AuthUserDetails) auth.getPrincipal();
        }
        return null;
    }

    public static Long getCurrentUserId() {
        AuthUserDetails user = getCurrentUser();
        return (user != null) ? user.getUserId() : null;
    }

    public static boolean hasRole(String role) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(role));
    }

    public static boolean isAuthenticated() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.isAuthenticated() && !(auth.getPrincipal() instanceof AnonymousAuthenticationToken);
    }

    public static String getUsernameOrDefault(String system) {
        return getCurrentUsername() != null ? getCurrentUsername() : system;
    }

    public static Long getUserIdOrDefault(long l) {
        return getCurrentUserId() != null ? getCurrentUserId() : l;
    }

    public static Long getTenantIdOrDefault(Long defaultTenantId) {
        AuthUserDetails user = getCurrentUser();
        return (user != null) ? user.getTenantId() : defaultTenantId;
    }

    /**
     * 获取当前登录用户的租户ID
     *
     * @return 租户ID,如果用户未登录则返回null
     */
    public static Long getTenantId() {
        AuthUserDetails user = getCurrentUser();
        return (user != null) ? user.getTenantId() : null;
    }

    public static Long getUserId() {
        return getCurrentUserId();
    }

    public static Long getDeptId() {
        return getCurrentUser() != null ? getCurrentUser().getDeptId() : null;
    }

    public static List<Long> getDeptAndChildDeptIds() {
        try {
            Long deptId = getDeptId();
            if (deptId == null) {
                return null;
            }

            // 通过Feign调用组织服务获取子部门ID列表
            // OrganizationDeptClient deptClient = BeanUtils.getBean(OrganizationDeptClient.class);
            // if (deptClient != null) {
            //     response deptClient.getAllChildDeptIds(deptId);
            // }

            // 如果无法获取服务，返回仅包含当前部门ID的列表
            return Collections.singletonList(deptId);
        } catch (Exception e) {
            return null;
        }
    }

    public static String getUsername() {
        return getCurrentUsername();
    }

    public static List<Long> getCustomDeptIds() {
        try {
            AuthUserDetails user = getCurrentUser();
            if (user == null) {
                return null;
            }

            // 从用户详情中获取自定义部门ID列表
            // 这里可以通过以下方式获取：
            // 1. 从AuthUserDetails中获取（如果已包含该字段）
            // 2. 通过Feign调用用户服务查询
            // 3. 从缓存中获取

            // 暂时返回null，待用户服务完善后实现
            // UserClient userClient = BeanUtils.getBean(UserClient.class);
            // if (userClient != null) {
            //     response userClient.getCustomDeptIds(user.getUserId());
            // }
            //TODO 用户服务完善后实现
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}

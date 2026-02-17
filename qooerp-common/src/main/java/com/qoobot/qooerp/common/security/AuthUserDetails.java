package com.qoobot.qooerp.common.security;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Spring Security 用户详情实现类
 * 基于 AuthUser 实体扩展，用于认证和授权
 */
@Data
public class AuthUserDetails implements UserDetails, Serializable {

    private static final long serialVersionUID = 1L;

    // --- 基础认证字段 ---
    private Long userId;
    private Long tenantId;      // 多租户 ID
    private String username;
    private String password;

    // --- 业务信息字段 ---
    private String realName;
    private String nickname;
    private String avatar;
    private String phone;
    private String email;
    private Integer accountType; // 0-系统用户，1-普通用户

    // --- 安全状态字段 ---
    private Integer status;      // 0-禁用，1-启用
    private Integer mfaEnabled;  // 0-未启用，1-已启用
    private String mfaSecretKey; // TOTP 密钥 (敏感信息，注意序列化安全)
    private LocalDateTime lockTime; // 锁定时间

    // --- 权限控制字段 (AuthUser 实体中通常不直接存储，需在此补充) ---
    private List<String> roles;       // 角色标识，如 ["ROLE_ADMIN"]
    private List<String> permissions; // 权限标识，如 ["user:add"]

    // --- 构造方法 ---

    public AuthUserDetails() {}

    /**
     * 完整构造方法
     */
    public AuthUserDetails(Long userId, Long tenantId, String username, String password,
                           String realName, String nickname, String avatar,
                           String phone, String email, Integer accountType,
                           Integer status, Integer mfaEnabled, String mfaSecretKey,
                           LocalDateTime lockTime, List<String> roles, List<String> permissions) {
        this.userId = userId;
        this.tenantId = tenantId;
        this.username = username;
        this.password = password;
        this.realName = realName;
        this.nickname = nickname;
        this.avatar = avatar;
        this.phone = phone;
        this.email = email;
        this.accountType = accountType;
        this.status = status;
        this.mfaEnabled = mfaEnabled;
        this.mfaSecretKey = mfaSecretKey;
        this.lockTime = lockTime;
        this.roles = roles != null ? roles : Collections.emptyList();
        this.permissions = permissions != null ? permissions : Collections.emptyList();
    }

    // --- UserDetails 接口实现 ---

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        // 添加角色
        if (roles != null) {
            authorities.addAll(roles.stream()
                    .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role)
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList()));
        }
        // 添加权限
        if (permissions != null) {
            authorities.addAll(permissions.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList()));
        }
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        // 可根据业务逻辑扩展，例如检查账户有效期
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // 如果 lockTime 不为空，且未过期（这里简化为只要有 lockTime 即视为锁定，具体看业务）
        // 更严谨的逻辑：lockTime != null && lockTime.plusMinutes(30).isAfter(LocalDateTime.now())
        return lockTime == null;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // 密码是否过期
        return true;
    }

    @Override
    public boolean isEnabled() {
        // 状态为 1 且未被锁定
        return status != null && status == 1 && isAccountNonLocked();
    }


    // --- 工具方法 ---

    public boolean hasRole(String role) {
        if (roles == null) return false;
        return roles.stream().anyMatch(r -> r.equals(role) || r.equals("ROLE_" + role));
    }

    public boolean hasPermission(String permission) {
        if (permissions == null) return false;
        return permissions.contains(permission);
    }

    public boolean isSystemAdmin() {
        return accountType != null && accountType == 0;
    }

    // --- 工厂方法 (用于从 AuthUser 实体转换) ---

    /**
     * 将 AuthUser 实体转换为 AuthUserDetails
     * 注意：roles 和 permissions 需要单独查询后传入
     */
    public static AuthUserDetails create(AuthUserDetails user, List<String> roles, List<String> permissions) {
        return new AuthUserDetails(
                user.getUserId(),
                user.getTenantId(),
                user.getUsername(),
                user.getPassword(),
                user.getRealName(),
                user.getNickname(),
                user.getAvatar(),
                user.getPhone(),
                user.getEmail(),
                user.getAccountType(),
                user.getStatus(),
                user.getMfaEnabled(),
                user.getMfaSecretKey(),
                user.getLockTime(),
                roles,
                permissions
        );
    }

    /**
     * 简化版工厂方法 (无权限信息，仅用于基础认证)
     */
    public static AuthUserDetails create(AuthUserDetails user) {
        return create(user, Collections.emptyList(), Collections.emptyList());
    }

    @Override
    public String toString() {
        return "AuthUserDetails{" +
                "userId=" + userId +
                ", tenantId=" + tenantId +
                ", username='" + username + '\'' +
                ", status=" + status +
                ", roles=" + roles +
                '}';
    }


    public Long getDeptId() {
        // TODO: 从数据库中查询
        Long deptId = null;

        return deptId;
    }
}
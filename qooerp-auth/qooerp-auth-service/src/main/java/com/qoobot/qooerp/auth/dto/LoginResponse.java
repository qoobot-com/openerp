package com.qoobot.qooerp.auth.dto;

import com.qoobot.qooerp.auth.entity.AuthUser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    /**
     * 访问令牌
     */
    private String accessToken;

    /**
     * 刷新令牌
     */
    private String refreshToken;

    /**
     * 令牌类型
     */
    @Builder.Default
    private String tokenType = "Bearer";

    /**
     * 过期时间（秒）
     */
    private Long expiresIn;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 是否需要MFA验证
     */
    private Boolean requireMfa;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 用户状态
     */
    private Integer status;

    /**
     * 转换为User实体
     */
    public AuthUser toEntity() {
        AuthUser user = new AuthUser();
        user.setId(this.userId);
        user.setUsername(this.username);
        user.setRealName(this.realName);
        user.setNickname(this.nickname);
        user.setAvatar(this.avatar);
        user.setPhone(this.phone);
        user.setEmail(this.email);
        user.setStatus(this.status);
        return user;
    }
}

package com.qoobot.qooerp.auth.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qoobot.qooerp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("auth_user")
public class AuthUser extends BaseEntity {

    /**
     * 用户ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

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
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 用户状态：0-禁用，1-启用
     */
    private Integer status;

    /**
     * 账户类型：0-系统用户，1-普通用户
     */
    private Integer accountType;

    /**
     * MFA是否启用：0-未启用，1-已启用
     */
    private Integer mfaEnabled;

    /**
     * MFA密钥（TOTP）
     */
    private String mfaSecretKey;

    /**
     * 最后登录IP
     */
    private String lastLoginIp;

    /**
     * 最后登录时间
     */
    private java.time.LocalDateTime lastLoginTime;

    /**
     * 登录失败次数
     */
    private Integer loginFailCount;

    /**
     * 账户锁定时间
     */
    private java.time.LocalDateTime lockTime;

    /**
     * 备注
     */
    private String remark;
}

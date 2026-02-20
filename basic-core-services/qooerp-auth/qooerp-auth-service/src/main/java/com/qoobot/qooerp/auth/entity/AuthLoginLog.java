package com.qoobot.qooerp.auth.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qoobot.qooerp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 登录日志实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("auth_login_log")
public class AuthLoginLog extends BaseEntity {

    /**
     * 日志ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 登录IP
     */
    private String loginIp;

    /**
     * 登录地点
     */
    private String loginLocation;

    /**
     * 浏览器
     */
    private String browser;

    /**
     * 操作系统
     */
    private String os;

    /**
     * 设备类型
     */
    private String device;

    /**
     * 登录状态：0-失败，1-成功
     */
    private Integer status;

    /**
     * 提示信息
     */
    private String message;

    /**
     * 登录时间
     */
    private java.time.LocalDateTime loginTime;
}

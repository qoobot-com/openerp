package com.qoobot.qooerp.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 在线用户实体类
 */
@Data
@TableName("system_online_user")
public class SystemOnlineUser {

    /**
     * 记录ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 登录令牌
     */
    private String token;

    /**
     * 登录IP
     */
    private String loginIp;

    /**
     * 登录地点
     */
    private String loginLocation;

    /**
     * 登录时间
     */
    private LocalDateTime loginTime;

    /**
     * 最后活跃时间
     */
    private LocalDateTime lastActiveTime;

    /**
     * 状态：0-已下线，1-在线
     */
    private Integer status;

    /**
     * 用户代理
     */
    private String userAgent;
}

package com.qoobot.qooerp.auth.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 设备实体
 */
@Data
@TableName("auth_device")
public class AuthDevice {

    /**
     * 设备ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 设备类型：PC/MOBILE/TABLET/APP
     */
    private String deviceType;

    /**
     * 设备指纹
     */
    private String deviceFingerprint;

    /**
     * 浏览器类型
     */
    private String browser;

    /**
     * 操作系统
     */
    private String os;

    /**
     * IP地址
     */
    private String ipAddress;

    /**
     * 登录地点
     */
    private String location;

    /**
     * 是否记住设备
     */
    private Boolean remembered;

    /**
     * 是否活跃
     */
    private Boolean active;

    /**
     * 最后活跃时间
     */
    private LocalDateTime lastActiveTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}

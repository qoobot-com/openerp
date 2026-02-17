package com.qoobot.qooerp.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 操作日志表实体
 *
 * @author QooERP Team
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("system_log")
public class SystemLog {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 用户名
     */
    @TableField("username")
    private String username;

    /**
     * 模块名称
     */
    @TableField("module")
    private String module;

    /**
     * 操作类型
     */
    @TableField("operation")
    private String operation;

    /**
     * 方法名称
     */
    @TableField("method")
    private String method;

    /**
     * 请求参数
     */
    @TableField("params")
    private String params;

    /**
     * 请求IP
     */
    @TableField("ip")
    private String ip;

    /**
     * 请求地点
     */
    @TableField("location")
    private String location;

    /**
     * 浏览器
     */
    @TableField("browser")
    private String browser;

    /**
     * 操作系统
     */
    @TableField("os")
    private String os;

    /**
     * 状态：0-失败 1-成功
     */
    @TableField("status")
    private Integer status;

    /**
     * 错误信息
     */
    @TableField("error_msg")
    private String errorMsg;

    /**
     * 执行时间（毫秒）
     */
    @TableField("cost_time")
    private Integer costTime;

    /**
     * 操作时间
     */
    @TableField(value = "operate_time", fill = FieldFill.INSERT)
    private LocalDateTime operateTime;
}

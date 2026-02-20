package com.qoobot.qooerp.system.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 操作日志DTO
 */
@Data
public class SystemLogDTO {

    /**
     * 日志ID
     */
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
     * 模块名称
     */
    private String module;

    /**
     * 操作类型
     */
    private String operation;

    /**
     * 方法名称
     */
    private String method;

    /**
     * 请求参数
     */
    private String params;

    /**
     * 请求IP
     */
    private String ip;

    /**
     * 请求地点
     */
    private String location;

    /**
     * 浏览器
     */
    private String browser;

    /**
     * 操作系统
     */
    private String os;

    /**
     * 状态：0-失败 1-成功
     */
    private Integer status;

    /**
     * 错误信息
     */
    private String errorMsg;

    /**
     * 执行时间（毫秒）
     */
    private Integer costTime;

    /**
     * 操作时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime operateTime;
}

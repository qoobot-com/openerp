package com.qoobot.qooerp.system.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 操作日志查询DTO
 */
@Data
public class SystemLogQueryDTO {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名（模糊查询）
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
     * 请求IP
     */
    private String ip;

    /**
     * 状态：0-失败 1-成功
     */
    private Integer status;

    /**
     * 操作开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    /**
     * 操作结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;
}

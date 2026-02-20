package com.qoobot.qooerp.system.dto;

import lombok.Data;

/**
 * 在线用户查询DTO
 */
@Data
public class SystemOnlineUserQueryDTO {

    /**
     * 用户名（模糊查询）
     */
    private String username;

    /**
     * 登录IP
     */
    private String loginIp;

    /**
     * 状态：0-已下线，1-在线
     */
    private Integer status;

    /**
     * 当前页
     */
    private Integer current = 1;

    /**
     * 每页大小
     */
    private Integer size = 10;
}

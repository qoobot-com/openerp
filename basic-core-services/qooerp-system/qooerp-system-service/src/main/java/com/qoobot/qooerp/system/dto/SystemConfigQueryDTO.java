package com.qoobot.qooerp.system.dto;

import lombok.Data;

/**
 * 系统参数查询DTO
 */
@Data
public class SystemConfigQueryDTO {

    /**
     * 参数键
     */
    private String configKey;

    /**
     * 参数名称（模糊查询）
     */
    private String configName;

    /**
     * 参数类型
     */
    private String configType;

    /**
     * 是否系统参数
     */
    private Integer isSystem;
}

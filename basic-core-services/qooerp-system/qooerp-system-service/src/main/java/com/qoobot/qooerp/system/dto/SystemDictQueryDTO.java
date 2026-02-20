package com.qoobot.qooerp.system.dto;

import lombok.Data;

/**
 * 字典查询DTO
 */
@Data
public class SystemDictQueryDTO {

    /**
     * 字典编码
     */
    private String dictCode;

    /**
     * 字典名称（模糊查询）
     */
    private String dictName;

    /**
     * 字典类型
     */
    private String dictType;

    /**
     * 状态：0-禁用 1-启用
     */
    private Integer status;
}

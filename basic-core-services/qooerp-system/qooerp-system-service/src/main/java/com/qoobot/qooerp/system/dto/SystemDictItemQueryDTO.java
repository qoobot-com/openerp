package com.qoobot.qooerp.system.dto;

import lombok.Data;

/**
 * 字典项查询DTO
 */
@Data
public class SystemDictItemQueryDTO {

    /**
     * 字典ID
     */
    private Long dictId;

    /**
     * 字典项标签（模糊查询）
     */
    private String itemLabel;

    /**
     * 字典项编码
     */
    private String itemCode;

    /**
     * 状态：0-禁用 1-启用
     */
    private Integer status;
}

package com.qoobot.qooerp.system.dto;

import lombok.Data;

/**
 * 导出任务查询DTO
 */
@Data
public class SystemExportTaskQueryDTO {

    /**
     * 当前页码
     */
    private Integer current = 1;

    /**
     * 每页条数
     */
    private Integer size = 10;

    /**
     * 模块名称
     */
    private String moduleName;

    /**
     * 状态：0-待执行，1-执行中，2-成功，3-失败
     */
    private Integer status;

    /**
     * 创建人ID
     */
    private Long creatorId;
}

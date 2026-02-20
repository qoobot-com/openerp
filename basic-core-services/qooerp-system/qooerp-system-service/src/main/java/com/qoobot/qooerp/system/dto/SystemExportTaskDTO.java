package com.qoobot.qooerp.system.dto;

import lombok.Data;

/**
 * 导出任务DTO
 */
@Data
public class SystemExportTaskDTO {

    /**
     * 任务ID
     */
    private Long id;

    /**
     * 任务名称
     */
    private String taskName;

    /**
     * 模块名称
     */
    private String moduleName;

    /**
     * 导出类型：excel/csv/pdf
     */
    private String exportType;

    /**
     * 导出条件（JSON格式）
     */
    private String exportCondition;
}

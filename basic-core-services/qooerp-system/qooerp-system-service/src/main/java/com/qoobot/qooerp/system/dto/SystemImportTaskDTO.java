package com.qoobot.qooerp.system.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * 导入任务DTO
 */
@Data
public class SystemImportTaskDTO {

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
     * 导入类型：excel/csv
     */
    private String importType;

    /**
     * 文件路径
     */
    private String filePath;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 上传文件
     */
    private MultipartFile file;
}
